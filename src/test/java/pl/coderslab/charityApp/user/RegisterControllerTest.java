package pl.coderslab.charityApp.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import pl.coderslab.charityApp.email.EmailService;

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

@WebMvcTest(RegisterController.class)
@ActiveProfiles("test")
@WithAnonymousUser
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class RegisterControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private UserService userServiceMock;
    @MockBean
    private EmailService emailServiceMock;
    private UserResource validUserRes;


    @BeforeEach
    void setUp() {
        final String email = "generous@test";
        validUserRes = UserResource.builder()
                .firstName("Jim")
                .lastName("Generous")
                .password("Password2020?")
                .password2("Password2020?")
                .email(email)
                .build();
        final BindingResult results = new BeanPropertyBindingResult(validUserRes, "userResource");
    }

    @Test
    void shouldPrepareRegisterPage() throws Exception {
        mockMvc.perform(get("/register"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("userResource", new UserResource()))
                .andExpect(view().name("/user/register"));
    }

    @Test
    void shouldSaveNewValidUser() throws Exception {
        mockMvc.perform(post("/register").with(csrf())
                .flashAttr("userResource", validUserRes))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        verify(userServiceMock).saveUser(validUserRes);
        verify(emailServiceMock).sendRegistrationConfirmation(validUserRes);
    }

    @Test
    void shouldNotSaveNotUniqueUserAndShouldAddEmailError() throws Exception {
        final UserResource duplicateUser = validUserRes.toBuilder().email("generous@test").build();
        final ConstraintViolation<String> violation = mock(ConstraintViolation.class);
        final Path mockPath = mock(Path.class);
        final Path.Node nodeMock = mock(Path.Node.class);
        final Iterator<Path.Node> iteratorMock = mock(Iterator.class);
        when(violation.getPropertyPath()).thenReturn(mockPath);
        when(mockPath.iterator()).thenReturn(iteratorMock);
        when(iteratorMock.next()).thenReturn(nodeMock);
        final Set<ConstraintViolation<String>> violations = Set.of(violation);
        doThrow(new ConstraintViolationException(violations))
                .when(userServiceMock).saveUser(duplicateUser);

        mockMvc.perform(post("/" +
                "register").with(csrf())
                .flashAttr("userResource", duplicateUser))
                .andExpect(status().isOk())
                .andExpect(view().name("/user/register"));
        verify(userServiceMock).saveUser(duplicateUser);
    }
}