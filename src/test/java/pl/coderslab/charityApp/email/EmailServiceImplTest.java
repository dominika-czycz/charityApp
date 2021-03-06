package pl.coderslab.charityApp.email;


import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.thymeleaf.spring5.SpringTemplateEngine;
import pl.coderslab.charityApp.category.Category;
import pl.coderslab.charityApp.donation.resources.DonationResource;
import pl.coderslab.charityApp.institution.Institution;
import pl.coderslab.charityApp.user.User;
import pl.coderslab.charityApp.user.UserService;
import pl.coderslab.charityApp.user.resources.OrdinaryUserResource;

import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;

import static org.mockito.Mockito.*;
@SpringBootTest
@ActiveProfiles("test")
class EmailServiceImplTest {
    @Autowired
    private EmailService testObject;
    @MockBean
    private JavaMailSender mailSenderMock;
    @MockBean
    private UserService userServiceMock;

    @Test
    void shouldSendRegistrationConfirmation() throws Exception {
        String email = "generous@test";
        String firstName = "Jim";
        Long id = 222L;
        final OrdinaryUserResource resource = OrdinaryUserResource.builder()
                .id(id)
                .firstName(firstName)
                .email(email)
                .build();
        final String uuid = "hkjhkjh6876876%^%^";
        when(userServiceMock.getUuid(id)).thenReturn(uuid);

        MimeMessage mimeMessage = new MimeMessage((Session) null);
        when(mailSenderMock.createMimeMessage()).thenReturn(mimeMessage);

        testObject.sendRegistrationConfirmation(resource);

        verify(mailSenderMock).send(mimeMessage);
    }

    @Test
    @WithMockUser("generous@test")
    void shouldSendDonationConfirmation() throws Exception {
        final Institution institution1 = Institution.builder().id(23L).name("Animals").build();
        final Category toys = Category.builder().id(11L).name("toys").build();
        final Category books = Category.builder().id(122L).name("books").build();
        final Set<Category> categories = Set.of(toys, books);
        final DonationResource donation = DonationResource.builder()
                .categories(categories)
                .institution(institution1)
                .city("Wrocław")
                .phoneNumber("+48 404 404 404")
                .pickUpDate(LocalDate.now().plusMonths(1))
                .pickUpTime(LocalTime.now())
                .street("Wrocławska")
                .quantity(2)
                .zipCode("34-333")
                .build();
        final User user = User.builder()
                .firstName("Jim")
                .lastName("Generous")
                .enabled(true)
                .email("generous@test").build();
        MimeMessage mimeMessage = new MimeMessage((Session) null);
        when(mailSenderMock.createMimeMessage()).thenReturn(mimeMessage);
        when(userServiceMock.getPrincipal()).thenReturn(user);
        testObject.sendDonationConfirmation(donation);
        verify(mailSenderMock).send(mimeMessage);
    }

}