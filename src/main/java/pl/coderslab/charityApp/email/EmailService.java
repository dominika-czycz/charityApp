package pl.coderslab.charityApp.email;

import pl.coderslab.charityApp.user.User;

import javax.mail.MessagingException;

public interface EmailService {
    void sendHTMLEmail(User user) throws MessagingException;
}
