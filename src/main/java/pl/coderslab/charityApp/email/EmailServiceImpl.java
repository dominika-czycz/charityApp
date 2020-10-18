package pl.coderslab.charityApp.email;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;
import pl.coderslab.charityApp.donation.DonationResource;
import pl.coderslab.charityApp.exceptions.NotExistingRecordException;
import pl.coderslab.charityApp.user.OrdinaryUserResource;
import pl.coderslab.charityApp.user.User;
import pl.coderslab.charityApp.user.UserService;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {
    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;
    private final UserService userService;

    @Override
    public void sendRegistrationConfirmation(OrdinaryUserResource resource) throws MessagingException, NotExistingRecordException {
        final Context thymeleafContext = new Context();
        final String uuid = userService.getUuid(resource.getId());
        final String link = "http://localhost:8080/register/confirm/" + uuid;
        thymeleafContext.setVariable("name", resource.getFirstName());
        thymeleafContext.setVariable("link", link);
        final String emailText = templateEngine.process("/email/email.html", thymeleafContext);

        final MimeMessage mimeMessage = mailSender.createMimeMessage();
        final MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

        helper.setFrom("noreplyDominika@gmail.com");
        helper.setTo(resource.getEmail());
        helper.setSubject("Welcome in charityApp!");
        helper.setText(emailText, true);
        ClassPathResource logo = new ClassPathResource("/static/images/icon-hands.png");
        ClassPathResource image = new ClassPathResource("/static/images/about-us.jpg");
        helper.addInline("logo", logo);
        helper.addInline("image", image);
        mailSender.send(mimeMessage);
    }

    @Override
    public void sendDonationConfirmation(DonationResource donation) throws MessagingException, NotExistingRecordException {
        final User user = userService.getPrincipal();
        final Context thymeleafContext = new Context();
        thymeleafContext.setVariable("name", user.getFirstName());
        thymeleafContext.setVariable("donation", donation);
        final String emailText = templateEngine.process("/email/donation-confirmation.html", thymeleafContext);

        final MimeMessage mimeMessage = mailSender.createMimeMessage();
        final MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

        helper.setFrom("noreplyDominika@gmail.com");
        helper.setTo(user.getEmail());
        helper.setSubject("Summary of your donation");
        helper.setText(emailText, true);
        ClassPathResource logo = new ClassPathResource("/static/images/icon-hands.png");
        helper.addInline("logo", logo);
        mailSender.send(mimeMessage);
    }
}
