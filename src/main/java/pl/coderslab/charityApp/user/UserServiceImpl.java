package pl.coderslab.charityApp.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.coderslab.charityApp.security.RoleRepository;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    @Override
    @Transactional
    public boolean save(User user) {
        if (isDuplicate(user)) {
            log.warn("Not unique email: {}", user.getEmail());
            return false;
        }
        encodePassword(user);
        user.addRole(roleRepository.findFirstByNameIgnoringCase("ROLE_USER"));
        final User saved = userRepository.save(user);
        log.debug("Entity {} has been saved", saved);
        return true;
    }

    private void encodePassword(User user) {
        final String encoded = passwordEncoder.encode(user.getPassword());
        user.setPassword(encoded);
    }

    private boolean isDuplicate(User user) {
        return userRepository.findFirstByEmailIgnoringCase(
                user.getEmail())
                .isPresent();
    }
}
