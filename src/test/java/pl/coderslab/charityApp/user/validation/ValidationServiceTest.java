package pl.coderslab.charityApp.user.validation;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import pl.coderslab.charityApp.user.User;
import pl.coderslab.charityApp.user.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
class ValidationServiceTest {
    @Autowired
    private ValidationService testObject;
    @MockBean
    private UserRepository userRepository;

    @Test
    void shouldReturnTrueForUnique() {
        String notExistingEmail = "notExisting@test";
        when(userRepository.existsByEmail(notExistingEmail)).thenReturn(false);

        final boolean uniqueEmail = testObject.isUniqueEmail(notExistingEmail);

        assertTrue(uniqueEmail);
    }

    @Test
    void shouldReturnFalseForNotUnique() {
        String existingEmail = "existing@test";
        when(userRepository.existsByEmail(existingEmail)).thenReturn(true);

        final boolean uniqueEmail = testObject.isUniqueEmail(existingEmail);

        assertFalse(uniqueEmail);
    }

    @Test
    void shouldReturnTrueForAlreadyExistingEmailIfConnectedWithId() {
        String existingEmail = "existing@test";
        Long id = 222L;
        final User user = User.builder()
                .email(existingEmail)
                .id(id).build();
        when(userRepository.findFirstByEmailIgnoringCase(existingEmail))
                .thenReturn(Optional.of(user));

        final boolean uniqueEmail = testObject.isUniqueEmail(existingEmail, id);
        assertTrue(uniqueEmail);
    }

    @Test
    void shouldReturnFalseForAlreadyExistingEmailIfNotConnectedWithId() {
        String existingEmail = "existing@test";
        Long userId = 222L;
        Long anotherUserId = 44L;
        final User user = User.builder()
                .email(existingEmail)
                .id(anotherUserId).build();
        when(userRepository.findFirstByEmailIgnoringCase(existingEmail))
                .thenReturn(Optional.of(user));

        final boolean uniqueEmail = testObject.isUniqueEmail(existingEmail, userId);
        assertFalse(uniqueEmail);
    }
}