package pl.coderslab.charityApp.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import pl.coderslab.charityApp.exceptions.NotExistingRecordException;
import pl.coderslab.charityApp.security.RoleRepository;

import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final UserAssembler userAssembler;

    @Override
    @Transactional
    public boolean save(UserResource userResource) {
        final User user = userAssembler.fromResource(userResource);
        log.debug("Preparing to save entity: {}...", user);
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

    @Override
    public String getPrincipalEmail() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email;
        if (principal instanceof UserDetails) {
            email = ((UserDetails) principal).getUsername();
        } else {
            email = principal.toString();
        }
        return email;
    }

    @Override
    public User getPrincipal() throws NotExistingRecordException {
        final String principalEmail = getPrincipalEmail();
        return userRepository.findFirstByEmailIgnoringCase(principalEmail).orElseThrow(
                new NotExistingRecordException("User with email " + principalEmail + " does not exist"));
    }

    @Override
    public boolean isValid(UserResource userResource, BindingResult result) {
        if (!result.hasErrors() && arePasswordsTheSame(userResource, result)) {
            return true;
        }
        log.warn("Resource {} fails validation. Return to register view.", userResource);
        return false;
    }

    @Override
    public boolean arePasswordsTheSame(UserResource userResource, BindingResult result) {
        if (Objects.equals(userResource.getPassword2(), userResource.getPassword())) {
            return true;
        }
        log.warn("Passwords 1: {}, 2: {} are not the same", userResource.getPassword(), userResource.getPassword2());
        final FieldError fieldError = new FieldError("password2", "password2", "Passwords are not the same!");
        result.addError(fieldError);
        return false;
    }

    @Override
    public UserResource getPrincipalResource() throws NotExistingRecordException {
        final User principal = getPrincipal();
        return userAssembler.toResource(principal);
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
