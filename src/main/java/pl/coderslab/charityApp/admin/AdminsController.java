package pl.coderslab.charityApp.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import pl.coderslab.charityApp.email.EmailService;
import pl.coderslab.charityApp.exceptions.NotExistingRecordException;
import pl.coderslab.charityApp.user.UserResource;
import pl.coderslab.charityApp.user.UserService;

import javax.mail.MessagingException;
import javax.validation.Valid;
import java.util.List;

@Controller
@Slf4j
@RequestMapping("/app/admin/admins")
@RequiredArgsConstructor
@SessionAttributes("userResource")
public class AdminsController {
    private final UserService userService;
    private final EmailService emailService;

    @GetMapping
    public String prepareListPage(Model model) {
        log.info("Looking for all admins list...");
        List<UserResource> admins = userService.findAllAdmins();
        model.addAttribute("admins", admins);
        log.debug("{} admins have been found", admins.size());
        return "/admin/admins/list";
    }

    @GetMapping("/add")
    public String prepareAddPage(Model model) {
        log.info("Preparing edit page ...");
        model.addAttribute("admin", new UserResource());
        return "/admin/admins/add";
    }

    @PostMapping("/add")
    public String processAddPage(@ModelAttribute("admin") @Valid UserResource admin, BindingResult result) throws MessagingException {
        if (result.hasErrors()) {
            log.warn("Resource {} fails validation", admin);
            return "/admin/admins/add";
        }
        final boolean isSavedUniqueUser = userService.saveAdmin(admin);
        if (isSavedUniqueUser) {
            emailService.sendRegistrationConfirmation(admin);
            return "redirect:/app/admin/admins";
        }
        final FieldError emailError = new FieldError("admin", "email", "Email is not unique!");
        result.addError(emailError);
        return "/admin/admins/add";
    }

    @GetMapping("/edit")
    public String prepareEditPage(@RequestParam Long id, Model model) throws NotExistingRecordException {
        log.debug("Preparing edit page for entity with id {}.", id);
        UserResource userResource = userService.getAdminResourceById(id);
        model.addAttribute("admin", userResource);
        return "/admin/admins/edit";
    }

    @PostMapping("/edit")
    public String processEditPage(@ModelAttribute("admin") @Valid UserResource admin, BindingResult result) throws NotExistingRecordException {
        if (result.hasErrors()) {
            log.warn("Resource {} fails validation", admin);
            return "/admin/admins/edit";
        }
        userService.editAdmin(admin);
        return "redirect:/app/admin/admins";
    }

    @GetMapping("/delete")
    public String prepareDeletePage(@RequestParam Long id, Model model) throws NotExistingRecordException {
        log.debug("Preparing delete page for entity with id {}.", id);
        if (!checkId(id)) return "redirect:/app/admin/admins";
        UserResource userResource = userService.getAdminResourceById(id);
        model.addAttribute("admin", userResource);
        return "/admin/admins/delete";
    }

    private boolean checkId(@RequestParam Long id) throws NotExistingRecordException {
        final Long principalId = userService.getPrincipal().getId();
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

    @ModelAttribute("userResource")
    public UserResource userResource(Model model) throws NotExistingRecordException {
        final Object userResource = model.getAttribute("userResource");
        return (userResource == null) ? userService.getPrincipalResource() : (UserResource) userResource;
    }
}
