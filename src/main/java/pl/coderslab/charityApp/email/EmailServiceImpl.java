package pl.coderslab.charityApp.email;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;
import pl.coderslab.charityApp.user.User;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {
    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;

    @Override
    public void sendHTMLEmail(User user) throws MessagingException {
        final Context thymeleafContext = new Context();
        thymeleafContext.setVariable("name", user.getFirstName());
        final String emailText = templateEngine.process("/email/email.html", thymeleafContext);

        final MimeMessage mimeMessage = mailSender.createMimeMessage();
        final MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

        helper.setFrom("noreplyDominika@gmail.com");
        helper.setTo(user.getEmail());
        helper.setSubject("Welcome in charityApp!");
        helper.setText(emailText, true);
        ClassPathResource logo = new ClassPathResource("/static/images/icon-hands.png");
        ClassPathResource image = new ClassPathResource("/static/images/about-us.jpg");
        helper.addInline("logo", logo);
        helper.addInline("image", image);
        mailSender.send(mimeMessage);
    }
}
