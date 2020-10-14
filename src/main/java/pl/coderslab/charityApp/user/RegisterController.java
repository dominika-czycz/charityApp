package pl.coderslab.charityApp.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import pl.coderslab.charityApp.email.EmailService;
import pl.coderslab.charityApp.user.validation.group.PreChecked;

import javax.mail.MessagingException;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Path;
import javax.validation.Valid;

@Controller
@Slf4j
@RequestMapping("/register")
@RequiredArgsConstructor
public class RegisterController {
    private final UserService userService;
    private final EmailService emailService;

    @GetMapping
    public String prepareRegisterPage(Model model) {
        log.info("Preparing to register...");
        model.addAttribute(new UserResource());
        return "/user/register";
    }

    @PostMapping
    public String processRegister(@Valid UserResource userResource,
                                  BindingResult result) throws MessagingException {
        log.debug("Resource to save: {}.", userResource);
        if (!isValid(userResource, result)) return "/user/register";
        try {
            userService.save(userResource);
        } catch (ConstraintViolationException cve) {
            log.warn("Business constraints were violated for {}", userResource);
            for (ConstraintViolation<?> violation : cve.getConstraintViolations()) {
                log.warn("Violation: {}", violation);
                String field = null;
                for (Path.Node node : violation.getPropertyPath()) {
                    field = node.getName();
                }
                result.rejectValue(field, "UniqueEmail.userResource.email");
            }
            return "/user/register";
        }
        emailService.sendRegistrationConfirmation(userResource);
        return "redirect:/";
    }

    private boolean isValid(UserResource userResource, BindingResult result) {
        if (!result.hasErrors()) {
            return true;
        }
        log.warn("Resource {} fails validation. Return to register view.", userResource);
        return false;
    }
}