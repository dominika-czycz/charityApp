package pl.coderslab.charityApp.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import pl.coderslab.charityApp.email.EmailService;
import pl.coderslab.charityApp.exceptions.NotExistingRecordException;
import pl.coderslab.charityApp.user.OrdinaryUserResource;
import pl.coderslab.charityApp.user.ToUpdateUserResource;
import pl.coderslab.charityApp.user.UserResource;
import pl.coderslab.charityApp.user.UserService;

import javax.mail.MessagingException;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Path;
import javax.validation.Valid;
import java.util.List;

@Controller
@Slf4j
@RequestMapping("/app/admin/admins")
@RequiredArgsConstructor
@SessionAttributes("userName")
public class AdminsController {
    private final UserService userService;
    private final EmailService emailService;

    @GetMapping
    public String prepareListPage(Model model) throws NotExistingRecordException {
        log.info("Looking for all admins list...");
        List<OrdinaryUserResource> admins = userService.findAllAdmins();
        model.addAttribute("admins", admins);
        final Long principalId = userService.getPrincipalResource().getId();
        model.addAttribute("principalId", principalId);
        log.debug("{} admins have been found", admins.size());
        return "/admin/admins/list";
    }

    @GetMapping("/add")
    public String prepareAddPage(Model model) {
        log.info("Preparing edit page ...");
        model.addAttribute("admin", new OrdinaryUserResource());
        return "/admin/admins/add";
    }

    @PostMapping("/add")
    public String processAddPage(@ModelAttribute("admin") @Valid OrdinaryUserResource admin, BindingResult result) throws MessagingException, NotExistingRecordException {
        if (result.hasErrors()) {
            log.warn("Resource {} fails validation", admin);
            return "/admin/admins/add";
        }
        try {
            userService.saveAdmin(admin);
        } catch (ConstraintViolationException cve) {
            setError(admin, result, cve);
            return "/admin/admins/add";
        }
        emailService.sendRegistrationConfirmation(admin);
        return "redirect:/app/admin/admins";
    }

    private static void setError(UserResource admin, BindingResult result, ConstraintViolationException cve) {
        log.warn("Email constraints have been violated for {}", admin);
        for (ConstraintViolation<?> violation : cve.getConstraintViolations()) {
            log.warn("Violation: {}", violation);
            String field = null;
            for (Path.Node node : violation.getPropertyPath()) {
                field = node.getName();
            }
            result.rejectValue(field, "UniqueEmail.userResource.email");
        }
    }

    @GetMapping("/edit")
    public String prepareEditPage(@RequestParam Long id, Model model) throws NotExistingRecordException {
        log.debug("Preparing edit page for entity with id {}.", id);
        ToUpdateUserResource userResource = userService.getToUpdateAdminResourceById(id);
        model.addAttribute("admin", userResource);
        return "/admin/admins/edit";
    }

    @PostMapping("/edit")
    public String processEditPage(@ModelAttribute("admin") @Valid ToUpdateUserResource admin, BindingResult result) throws NotExistingRecordException {
        if (result.hasErrors()) {
            log.warn("Resource {} fails validation", admin);
            return "/admin/admins/edit";
        }

        if (changePassword(admin, result)) return "/admin/admins/edit";

        if (editAdmin(admin, result)) return "/admin/admins/edit";
        return "redirect:/app/admin/admins";
    }

    private boolean editAdmin(@ModelAttribute("admin") @Valid ToUpdateUserResource admin, BindingResult result) throws NotExistingRecordException {
        try {
            userService.editAdmin(admin);
        } catch (ConstraintViolationException cve) {
            setError(admin, result, cve);
            return true;
        }
        return false;
    }

    private boolean changePassword(ToUpdateUserResource admin, BindingResult result) throws NotExistingRecordException {
        if (admin.getPassword2() != null && !admin.getPassword2().isBlank()) {
            try {
                userService.changePassword(admin);
            } catch (ConstraintViolationException cve) {
                setErrorForPassword2(admin, result, cve);
                return true;
            }
        }
        return false;
    }

    private static void setErrorForPassword2(ToUpdateUserResource userResource, BindingResult result, ConstraintViolationException cve) {
        log.warn("Password constraints have been violated for {}", userResource);
        for (ConstraintViolation<?> violation : cve.getConstraintViolations()) {
            log.warn("Violation: {}", violation);
            String field = null;
            for (Path.Node node : violation.getPropertyPath()) {
                field = node.getName();
            }
            result.rejectValue(field, "SamePassword.userResource.password2");
        }
    }


    @GetMapping("/delete")
    public String prepareDeletePage(@RequestParam Long id, Model model) throws NotExistingRecordException {
        log.debug("Preparing delete page for entity with id {}.", id);
        if (!checkId(id)) return "redirect:/app/admin/admins";
        OrdinaryUserResource userResource = userService.getAdminResourceById(id);
        model.addAttribute("admin", userResource);
        return "/admin/admins/delete";
    }

    private boolean checkId(@RequestParam Long id) throws NotExistingRecordException {
        final Long principalId = userService.getPrincipalResource().getId();
        if (principalId.equals(id)) {
            log.warn("Admin's to delete id is the same as principal id {}", id);
            return false;
        }
        return true;
    }

    @PostMapping("/delete")
    public String processDeletePage(@RequestParam Long id) throws NotExistingRecordException {
        if (checkId(id)) {
            userService.deleteAdmin(id);
        }
        return "redirect:/app/admin/admins";
    }


    @ModelAttribute("userName")
    public String userResource(Model model) throws NotExistingRecordException {
        final Object userName = model.getAttribute("userName");
        return (userName == null) ? userService.getPrincipalResource().getFirstName() : (String) userName;
    }
}
