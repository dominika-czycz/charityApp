package pl.coderslab.charityApp.user.validation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.coderslab.charityApp.user.UserRepository;

@Service
@RequiredArgsConstructor
public class ValidationServiceImpl implements ValidationService {
    private final UserRepository userRepository;

    @Override
    public boolean isUniqueEmail(String email) {
        return !userRepository.existsByEmail(email);
    }
}
