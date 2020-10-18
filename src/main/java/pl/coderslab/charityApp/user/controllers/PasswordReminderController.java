package pl.coderslab.charityApp.user.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import pl.coderslab.charityApp.email.EmailService;
import pl.coderslab.charityApp.exceptions.NotExistingRecordException;
import pl.coderslab.charityApp.user.UserService;
import pl.coderslab.charityApp.user.resources.ToChangePasswordUserResource;

import javax.mail.MessagingException;
import javax.validation.Valid;

@Controller
@Slf4j
@RequestMapping("/password-reminder")
@RequiredArgsConstructor
public class PasswordReminderController {
    private final UserService userService;
    private final EmailService emailService;

    @GetMapping
    public String prepareRemindPasswordPage() {
        return "/remind-password";
    }

    @PostMapping
    public String procesRemindPasswordPage(@RequestParam String email, Model model) throws NotExistingRecordException, MessagingException {
        final ToChangePasswordUserResource userResource = userService.findByEmail(email);
        userService.setUuid(userResource);
        emailService.sendPasswordResetLink(userResource);
        model.addAttribute("passwordLinkMessage", true);
        return "/remind-password";
    }

    @GetMapping("/change")
    public String prepareChangePasswordPage(@RequestParam String uuid, Model model) throws NotExistingRecordException {
        final ToChangePasswordUserResource user = userService.getUserToChangePasswordByUuid(uuid);
        log.debug("Resource to change password {}", user);
        model.addAttribute("user", user);
        return "/change-password";
    }

    @PostMapping("/change")
    public String processChangePasswordPage(@ModelAttribute("user") @Valid ToChangePasswordUserResource user,
                                            BindingResult bindingResult) throws NotExistingRecordException {
        if (bindingResult.hasErrors()) {
            log.warn("Resource {} fails validation!", user);
            return "/change-password";
        }
        userService.changePassword(user);
        return "redirect:/";
    }

}
