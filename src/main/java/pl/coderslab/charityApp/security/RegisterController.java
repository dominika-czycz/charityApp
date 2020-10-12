package pl.coderslab.charityApp.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import pl.coderslab.charityApp.email.EmailService;
import pl.coderslab.charityApp.user.User;
import pl.coderslab.charityApp.user.UserService;

import javax.mail.MessagingException;
import javax.validation.Valid;
import java.util.Objects;

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
        model.addAttribute(new User());
        return "/user/register";
    }

    @PostMapping
    public String processRegister(@RequestParam(name = "password2") String password2,
                                  @Valid User user,
                                  BindingResult result,
                                  Model model) throws MessagingException {
        log.debug("Entity to save {}", user);
        if (!isValid(user, result)) return "/user/register";
        if (!arePasswordsTheSame(password2, user, model)) return "/user/register";
        final boolean isSavedUniqueUser = userService.save(user);
        if (isSavedUniqueUser) {
            emailService.sendHTMLEmail(user);
            return "redirect:/";
        }
        log.debug("Entity {} is not unique. Return to register view.", user);
        model.addAttribute("errorMessage", "Username is not unique.");
        return "/user/register";
    }

    private boolean isValid(User user, BindingResult result) {
        if (result.hasErrors()) {
            log.warn("Entity {} fails validation. Return to register view.", user);
            return false;
        }
        return true;
    }

    private boolean arePasswordsTheSame(String repeatedPassword, User user, Model model) {
        if (!Objects.equals(repeatedPassword, user.getPassword())) {
            log.warn("Passwords 1: {}, 2: {} are not the same", user.getPassword(), repeatedPassword);
            model.addAttribute("passwordMessage", "Repeated password is not the same!");
            return false;
        }
        return true;
    }

}