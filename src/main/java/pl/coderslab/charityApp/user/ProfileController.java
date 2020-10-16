package pl.coderslab.charityApp.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import pl.coderslab.charityApp.exceptions.NotExistingRecordException;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Path;
import javax.validation.Valid;

@Controller
@Slf4j
@RequestMapping("/app/profile")
@RequiredArgsConstructor
@SessionAttributes("userName")
public class ProfileController {
    private final UserService userService;

    @GetMapping
    public String prepareEditPage(Model model) throws NotExistingRecordException {
        final ToUpdateUserResource principalResource = userService.getPrincipalToUpdateResource();
        log.debug("Preparing edit page for entity {}.", principalResource);
        model.addAttribute("user", principalResource);
        return "/user/edit";
    }

    @PostMapping
    public String processEditPage(@ModelAttribute("user") @Valid ToUpdateUserResource user,
                                  BindingResult result, Model model) throws NotExistingRecordException {
        if (result.hasErrors()) {
            log.warn("Resource {} fails validation", user);
            return "/user/edit";
        }

        setPrincipalId(user);

        if (changePassword(user, result)) return "/user/edit";

        if (editUser(user, result)) return "/user/edit";

        model.addAttribute("userName", user.getFirstName());
        return "redirect:/app/donation";
    }

    private boolean editUser(@ModelAttribute("user") @Valid ToUpdateUserResource user, BindingResult result) throws NotExistingRecordException {
        try {
            userService.editUser(user);
        } catch (ConstraintViolationException cve) {
            setErrorForEmail(user, result, cve);
            return true;
        }
        return false;
    }

    private void setPrincipalId(@ModelAttribute("user") @Valid ToUpdateUserResource user) throws NotExistingRecordException {
        final Long id = userService.getPrincipalResource().getId();
        user.setId(id);
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

    private static void setErrorForEmail(ToUpdateUserResource userResource, BindingResult result, ConstraintViolationException cve) {
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

    @ModelAttribute("userName")
    public String userResource(Model model) throws NotExistingRecordException {
        final Object userName = model.getAttribute("userName");
        return (userName == null) ? userService.getPrincipalResource().getFirstName() : (String) userName;
    }
}
