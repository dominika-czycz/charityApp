package pl.coderslab.charityApp.user.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import pl.coderslab.charityApp.exceptions.NotExistingRecordException;
import pl.coderslab.charityApp.user.UserService;
import pl.coderslab.charityApp.user.resources.OrdinaryUserResource;
import pl.coderslab.charityApp.user.resources.ToUpdateUserResource;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Path;
import javax.validation.Valid;
import java.util.List;

@Controller
@Slf4j
@RequestMapping("/app/admin/users")
@RequiredArgsConstructor
@SessionAttributes("userName")
public class UsersController {
    private final UserService userService;

    @GetMapping
    public String prepareListPage(Model model) throws NotExistingRecordException {
        log.info("Looking for all users list...");
        List<OrdinaryUserResource> users = userService.findAllUsers();
        model.addAttribute("users", users);
        final Long principalId = userService.getPrincipalResource().getId();
        model.addAttribute("principalId", principalId);
        log.debug("{} users have been found", users.size());
        return "/admin/users/list";
    }

    @GetMapping("/edit")
    public String prepareEditPage(@RequestParam Long id, Model model) throws NotExistingRecordException {
        log.debug("Preparing edit page for entity with id {}.", id);
        ToUpdateUserResource userResource = userService.getToUpdateUserResourceById(id);
        model.addAttribute("user", userResource);
        return "/admin/users/edit";
    }

    @PostMapping("/edit")
    public String processEditPage(@ModelAttribute("user") @Valid ToUpdateUserResource user, BindingResult result) throws NotExistingRecordException {
        if (result.hasErrors()) {
            log.warn("Resource {} fails validation", user);
            return "/admin/users/edit";
        }
        if (changePassword(user, result)) return "/admin/users/edit";
        try {
            userService.editUser(user);
        } catch (ConstraintViolationException cve) {
            setError(user, result, cve);
            return "/admin/users/edit";
        }
        return "redirect:/app/admin/users";
    }

    private boolean changePassword(ToUpdateUserResource user, BindingResult result) throws NotExistingRecordException {
        if (user.getPassword2() != null && !user.getPassword2().isBlank()) {
            try {
                userService.changePassword(user);
            } catch (ConstraintViolationException cve) {
                setErrorForPassword2(user, result, cve);
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

    @GetMapping("/block")
    public String prepareBlockPage(@RequestParam Long id, Model model) throws NotExistingRecordException {
        log.debug("Preparing block page for entity with id {}.", id);
        if (!checkId(id)) return "redirect:/app/admin/users";
        OrdinaryUserResource userResource = userService.getUserResourceById(id);
        model.addAttribute("user", userResource);
        return "/admin/users/block";
    }

    @PostMapping("/block")
    public String processBlockPage(@RequestParam Long id) throws NotExistingRecordException {
        if (checkId(id)) {
            userService.blockUser(id);
        }
        return "redirect:/app/admin/users";
    }

    @GetMapping("/delete")
    public String prepareDeletePage(@RequestParam Long id, Model model) throws NotExistingRecordException {
        log.debug("Preparing delete page for entity with id {}.", id);
        if (!checkId(id)) return "redirect:/app/admin/users";
        OrdinaryUserResource userResource = userService.getUserResourceById(id);
        model.addAttribute("user", userResource);
        return "/admin/users/delete";
    }

    @PostMapping("/delete")
    public String processDeletePage(@RequestParam Long id) throws NotExistingRecordException {
        if (checkId(id)) {
            userService.deleteUser(id);
        }
        return "redirect:/app/admin/users";
    }

    private boolean checkId(@RequestParam Long id) throws NotExistingRecordException {
        final Long principalId = userService.getPrincipalResource().getId();
        if (principalId.equals(id)) {
            log.warn("User's to delete id is the same as principal id {}", id);
            return false;
        }
        return true;
    }

    private static void setError(ToUpdateUserResource userResource, BindingResult result, ConstraintViolationException cve) {
        log.warn("Email constraints have been violated for {}", userResource);
        for (ConstraintViolation<?> violation : cve.getConstraintViolations()) {
            log.warn("Violation: {}", violation);
            String field = null;
            for (Path.Node node : violation.getPropertyPath()) {
                field = node.getName();
            }
            result.rejectValue(field, "UniqueEmail.userResource.email");
        }
    }

    @ModelAttribute("userName")
    public String userResource(Model model) throws NotExistingRecordException {
        final Object userName = model.getAttribute("userName");
        return (userName == null) ? userService.getPrincipalResource().getFirstName() : (String) userName;
    }
}
