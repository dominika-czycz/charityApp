package pl.coderslab.charityApp.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import pl.coderslab.charityApp.exceptions.NotExistingRecordException;

import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
class UserServiceTest {
    @Autowired
    private UserService testObject;
    @Autowired
    private UserAssembler userAssembler;
    @MockBean
    private UserRepository userRepositoryMock;
    @MockBean
    private PasswordEncoder passwordEncoderMock;

    private User user;
    private final String email = "generous@test";
    private UserResource validUserRes;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .firstName("Jim")
                .lastName("Generous")
                .enabled(true)
                .password("Password2020?")
                .email(email).build();
        validUserRes = userAssembler.toResource(user);
        validUserRes.setPassword2(user.getPassword());
    }

    @Test
    @WithMockUser("generous@test")
    void shouldReturnPrincipalEmail() {
        final String principalEmail = testObject.getPrincipalEmail();

        assertThat(principalEmail, is(email));
    }

    @Test
    @WithMockUser("generous@test")
    void shouldReturnPrincipal() throws NotExistingRecordException {
        when(userRepositoryMock.findFirstByEmailIgnoringCase(email))
                .thenReturn(Optional.ofNullable(user));
        final User principal = testObject.getPrincipal();

        assertThat(principal, is(user));
    }
    @Test
    @WithMockUser("generous@test")
    void shouldThrowException()  {
        when(userRepositoryMock.findFirstByEmailIgnoringCase(email))
                .thenReturn(Optional.empty());
        assertThrows(NotExistingRecordException.class,
                ()-> testObject.getPrincipal());
    }

    @Test
    @WithAnonymousUser
    void shouldSaveUserEntity() {
        final String encodedPassword = "%&^%*&^%798798BJHJH";
        when(passwordEncoderMock.encode(user.getPassword()))
                .thenReturn(encodedPassword);

        testObject.save(validUserRes);
        verify(passwordEncoderMock).encode(user.getPassword());
        verify(userRepositoryMock).save(user);
    }


}