package pl.coderslab.charityApp.user.validation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.coderslab.charityApp.user.User;
import pl.coderslab.charityApp.user.UserRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ValidationServiceImpl implements ValidationService {
    private final UserRepository userRepository;

    @Override
    public boolean isUniqueEmail(String email) {
        return !userRepository.existsByEmail(email);
    }

    @Override
    public boolean isUniqueEmail(String email, Long excludedId) {
        final Optional<User> user =
                userRepository.findFirstByEmailIgnoringCase(email);
        if (user.isEmpty()) return true;
        return user.get().getId().equals(excludedId);
    }
}
