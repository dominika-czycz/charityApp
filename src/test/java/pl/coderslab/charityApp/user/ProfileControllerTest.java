package pl.coderslab.charityApp.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import pl.coderslab.charityApp.exceptions.NotExistingRecordException;
import pl.coderslab.charityApp.user.controllers.ProfileController;
import pl.coderslab.charityApp.user.resources.OrdinaryUserResource;
import pl.coderslab.charityApp.user.resources.ToUpdateUserResource;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Path;
import java.util.Iterator;
import java.util.Set;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProfileController.class)
@ActiveProfiles("test")
@WithMockUser(roles = "ADMIN", username = "admin@test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ProfileControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private UserService userServiceMock;

    @BeforeEach
    void setUp() throws NotExistingRecordException {
        final String email = "user@test";
        final OrdinaryUserResource ordinaryUserResource = OrdinaryUserResource.builder()
                .id(1112L)
                .firstName("Jim")
                .lastName("Generous")
                .password("Password2020?")
                .password2("Password2020?")
                .email(email)
                .enabled(true)
                .build();
        when(userServiceMock.getPrincipalResource()).thenReturn(ordinaryUserResource);
    }

    @Test
    void shouldPrepareEditPage() throws Exception {
        final ToUpdateUserResource toEdit = ToUpdateUserResource.builder()
                .id(2222L)
                .firstName("Jack")
                .lastName("Helpful")
                .email("helpful@test")
                .build();
        when(userServiceMock.getPrincipalToUpdateResource()).thenReturn(toEdit);

        mockMvc.perform(get("/app/profile"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(model().attribute("user", toEdit))
                .andExpect(view().name("/user/edit"));
        verify(userServiceMock).getPrincipalResource();
    }

    @Test
    void shouldUpdateEntityFromValidResource() throws Exception {
        final Long id = 2222L;
        final ToUpdateUserResource resourceWithNewData = ToUpdateUserResource.builder()
                .id(id)
                .firstName("Jack")
                .lastName("Helpful")
                .password("Password2021?")
                .password2("Password2021?")
                .email("helpful@test")
                .build();

        mockMvc.perform(post("/app/profile").with(csrf())
                .flashAttr("user", resourceWithNewData))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/app/donation"));
        verify(userServiceMock).changePassword(resourceWithNewData);
        verify(userServiceMock).editUser(resourceWithNewData);
    }


    @Test
    void shouldNotUpdateFromInvalidResource() throws Exception {
        final ToUpdateUserResource invalidResource = new ToUpdateUserResource();

        mockMvc.perform(post("/app/profile").with(csrf())
                .flashAttr("user", invalidResource))
                .andExpect(status().is2xxSuccessful())
                .andExpect(model().errorCount(3))
                .andExpect(model().attributeHasFieldErrors(
                        "user", "firstName", "lastName", "email"))
                .andExpect(view().name("/user/edit"));
        verify(userServiceMock, atMost(0)).changePassword(invalidResource);
        verify(userServiceMock, atMost(0)).editUser(invalidResource);
    }

    @Test
    void shouldNotUpdateFromResourceWithInvalidPassword() throws Exception {
        final Long id = 2222L;
        final ToUpdateUserResource invalidResource = ToUpdateUserResource.builder()
                .id(id)
                .firstName("Jack")
                .lastName("Helpful")
                .password("password2021?")
                .password2("password2021?")
                .email("helpful@test")
                .build();

        mockMvc.perform(post("/app/profile").with(csrf())
                .flashAttr("user", invalidResource))
                .andExpect(status().is2xxSuccessful())
                .andExpect(model().errorCount(1))
                .andExpect(model().attributeHasFieldErrors(
                        "user", "password"))
                .andExpect(view().name("/user/edit"));
        verify(userServiceMock, atMost(0)).changePassword(invalidResource);
        verify(userServiceMock, atMost(0)).editUser(invalidResource);
    }

    @Test
    void shouldNotEditEntityWithNotUniqueUserEmail() throws Exception {
        final ToUpdateUserResource duplicateUser = ToUpdateUserResource.builder()
                .id(222L)
                .firstName("Jack")
                .lastName("Helpful")
                .password("Password2021?")
                .password2("Password2021?")
                .email("helpful@test")
                .build();

        final ConstraintViolation<String> violation = mock(ConstraintViolation.class);
        final Path mockPath = mock(Path.class);
        final Path.Node nodeMock = mock(Path.Node.class);
        final Iterator<Path.Node> iteratorMock = mock(Iterator.class);
        when(violation.getPropertyPath()).thenReturn(mockPath);
        when(mockPath.iterator()).thenReturn(iteratorMock);
        when(iteratorMock.next()).thenReturn(nodeMock);
        final Set<ConstraintViolation<String>> violations = Set.of(violation);
        doThrow(new ConstraintViolationException(violations))
                .when(userServiceMock).editUser(duplicateUser);

        mockMvc.perform(post("/app/profile").with(csrf())
                .flashAttr("user", duplicateUser))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("/user/edit"));
        verify(userServiceMock).changePassword(duplicateUser);
        verify(userServiceMock).editUser(duplicateUser);
    }

    @Test
    void shouldNotUpdatePasswordIfNotTheSame() throws Exception {
        final Long id = 2222L;
        final ToUpdateUserResource userWithNotTheSamePasswd = ToUpdateUserResource.builder()
                .id(id)
                .firstName("Jack")
                .lastName("Helpful")
                .password("Password2021?")
                .password2("Passwordjafkljlk1?")
                .email("helpful@test")
                .build();
        final ConstraintViolation<String> violation = mock(ConstraintViolation.class);
        final Path mockPath = mock(Path.class);
        final Path.Node nodeMock = mock(Path.Node.class);
        final Iterator<Path.Node> iteratorMock = mock(Iterator.class);
        when(violation.getPropertyPath()).thenReturn(mockPath);
        when(mockPath.iterator()).thenReturn(iteratorMock);
        when(iteratorMock.next()).thenReturn(nodeMock);
        final Set<ConstraintViolation<String>> violations = Set.of(violation);
        doThrow(new ConstraintViolationException(violations))
                .when(userServiceMock).changePassword(userWithNotTheSamePasswd);

        mockMvc.perform(post("/app/profile").with(csrf())
                .flashAttr("user", userWithNotTheSamePasswd))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("/user/edit"));
        verify(userServiceMock).changePassword(userWithNotTheSamePasswd);
    }


}