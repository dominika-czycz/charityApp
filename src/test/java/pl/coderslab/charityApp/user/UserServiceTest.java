package pl.coderslab.charityApp.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import pl.coderslab.charityApp.exceptions.NotExistingRecordException;

import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
class UserServiceTest {
    @Autowired
    private UserService testObject;
    @MockBean
    private UserRepository userRepositoryMock;

    @Test
    @WithMockUser("generous@test")
    void shouldReturnPrincipalEmail() {
        final String expected = "generous@test";
        final String principalEmail = testObject.getPrincipalEmail();
        assertThat(principalEmail, is(expected));
    }

    @Test
    @WithMockUser("generous@test")
    void shouldReturnPrincipal() throws NotExistingRecordException {
        final String email = "generous@test";
        final User user = User.builder()
                .firstName("Jim")
                .lastName("Generous")
                .enabled(true)
                .email(email).build();
        when(userRepositoryMock.findFirstByEmailIgnoringCase(email))
                .thenReturn(Optional.ofNullable(user));
        final User principal = testObject.getPrincipal();
        assertThat(principal,is(user));
    }
}