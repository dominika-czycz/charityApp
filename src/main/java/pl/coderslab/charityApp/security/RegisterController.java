package pl.coderslab.charityApp.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import pl.coderslab.charityApp.email.EmailService;
import pl.coderslab.charityApp.user.User;
import pl.coderslab.charityApp.user.UserResource;
import pl.coderslab.charityApp.user.UserService;

import javax.mail.MessagingException;
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
        if (!userService.isValid(userResource, result)) return "/user/register";
        final boolean isSavedUniqueUser = userService.save(userResource);

        if (isSavedUniqueUser) {
            emailService.sendRegistrationConfirmation(userResource);
            return "redirect:/";
        }
        new ObjectError("email", "Email is not unique!");
        return "/user/register";
    }
}