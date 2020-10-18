package pl.coderslab.charityApp.user.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import pl.coderslab.charityApp.email.EmailService;
import pl.coderslab.charityApp.exceptions.NotExistingRecordException;
import pl.coderslab.charityApp.user.UserService;
import pl.coderslab.charityApp.user.resources.ToChangePasswordUserResource;
import pl.coderslab.charityApp.user.resources.ToUpdateUserResource;

import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.mockito.Mockito.atMost;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PasswordReminderController.class)
@ActiveProfiles("test")
@WithAnonymousUser
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class PasswordReminderControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private UserService userServiceMock;
    @MockBean
    private EmailService emailServiceMock;

    private ToChangePasswordUserResource validUserRes;
    final String email = "generous@test";
    final String uuid = UUID.randomUUID().toString();

    @BeforeEach
    void setUp() {
        validUserRes = ToChangePasswordUserResource.builder()
                .id(222L)
                .firstName("Jim")
                .lastName("Generous")
                .password("Password2020?")
                .password2("Password2020?")
                .email(email)
                .build();
    }

    @Test
    void shouldPrepareRemindPasswordPage() throws Exception {
        mockMvc.perform(get("/password-reminder"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("/remind-password"));
    }

    @Test
    void shouldSendLinkForValidEmail() throws Exception {
        when(userServiceMock.findByEmail(email))
                .thenReturn(validUserRes);

        mockMvc.perform(post("/password-reminder").with(csrf())
                .param("email", email))
                .andExpect(status().is2xxSuccessful())
                .andExpect(model().attribute("passwordLinkMessage", true))
                .andExpect(view().name("/remind-password"));
        verify(userServiceMock).setUuid(validUserRes);
        verify(emailServiceMock).sendPasswordResetLink(validUserRes);
    }

    @Test
    void shouldNotSendLinkForInvalidEmailButRedirectToErrorPage() throws Exception {
        when(userServiceMock.findByEmail(email))
                .thenThrow(NotExistingRecordException.class);

        mockMvc.perform(post("/password-reminder").with(csrf())
                .param("email", email))
                .andExpect(status().is2xxSuccessful())
        .andExpect(view().name("/error/error"));
    }

    @Test
    void shouldPrepareChangePasswordPage() throws Exception {
        validUserRes.setUuid(uuid);
        when(userServiceMock.getUserToChangePasswordByUuid(uuid))
                .thenReturn(validUserRes);

        mockMvc.perform(get("/password-reminder/change")
                .param("uuid", uuid))
                .andExpect(status().is2xxSuccessful())
                .andExpect(model().attribute("user", validUserRes))
                .andExpect(view().name("/change-password"));

        verify(userServiceMock).getUserToChangePasswordByUuid(uuid);
    }

    @Test
    void shouldProcesPasswordChangingForValidResource() throws Exception {
        validUserRes.setUuid(uuid);
        mockMvc.perform(post("/password-reminder/change").with(csrf())
                .flashAttr("user", validUserRes))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
        verify(userServiceMock).changePassword(validUserRes);
    }

    @Test
    void shouldNoChangePasswordForResourceWithInvalidPasswordAndWithoutUuid() throws Exception {
        final Long id = 2222L;
        final ToChangePasswordUserResource invalidResource = ToChangePasswordUserResource.builder()
                .id(id)
                .firstName("Jack")
                .lastName("Helpful")
                .password("password2021?")
                .password2("password2021?")
                .email("helpful@test")
                .build();

        mockMvc.perform(post("/password-reminder/change").with(csrf())
                .flashAttr("user", invalidResource))
                .andExpect(status().is2xxSuccessful())
                .andExpect(model().errorCount(2))
                .andExpect(model().attributeHasFieldErrors(
                        "user", "password", "uuid"))
                .andExpect(view().name("/change-password"));
        verify(userServiceMock, atMost(0)).changePassword(invalidResource);
    }


}