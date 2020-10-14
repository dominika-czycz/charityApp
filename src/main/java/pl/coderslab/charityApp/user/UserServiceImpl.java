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
import org.springframework.validation.annotation.Validated;
import pl.coderslab.charityApp.exceptions.NotExistingRecordException;
import pl.coderslab.charityApp.security.Role;
import pl.coderslab.charityApp.security.RoleRepository;
import pl.coderslab.charityApp.user.validation.group.PreChecked;

import javax.validation.Valid;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Validated
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final UserAssembler userAssembler;

    @Override
    @Transactional
    @Validated(PreChecked.class)
    public void save(@Valid UserResource userResource) {
        final User user = getUser(userResource, "ROLE_USER");
        save(user);
    }

    @Override
    @Transactional
    @Validated(PreChecked.class)
    public void saveAdmin(@Valid UserResource userResource) {
        final User user = getUser(userResource, "ROLE_ADMIN");
        save(user);
    }

    private User getUser(UserResource userResource, String role_user) {
        final User user = userAssembler.fromResource(userResource);
        user.addRole(roleRepository.findFirstByNameIgnoringCase(role_user));
        return user;
    }

    private void save(User user) {
        log.debug("Preparing to save entity: {}...", user);
        encodePassword(user);
        final User saved = userRepository.save(user);
        log.debug("Entity {} has been saved", saved);
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
    public UserResource getPrincipalResource() throws NotExistingRecordException {
        final User principal = getPrincipal();
        return userAssembler.toResource(principal);
    }

    @Override
    public List<UserResource> findAll() {
        return userRepository
                .findAll()
                .stream()
                .map(userAssembler::toResource)
                .collect(Collectors.toList());
    }

    @Override
    public UserResource getAdminResourceById(Long id) throws NotExistingRecordException {
        final Role roleAdmin = roleRepository.findFirstByNameIgnoringCase("ROLE_ADMIN");
        return userRepository.findById(id)
                .filter(user -> user.getRoles().contains(roleAdmin))
                .map(userAssembler::toResource)
                .orElseThrow(
                        new NotExistingRecordException("Admin with id " + id + " does not exist!"));
    }

    @Override
    public void deleteAdmin(Long id) throws NotExistingRecordException {
        log.debug("Preparing to delete entity with id {}...", id);
        final User toDelete = getAdmin(id);
        log.debug("Deleting entity: {}....", toDelete);
        userRepository.delete(toDelete);
        log.debug("Entity {} has been deleted.", toDelete);
    }

    private User getAdmin(Long id) throws NotExistingRecordException {
        final Role roleAdmin = roleRepository.findFirstByNameIgnoringCase("ROLE_ADMIN");
        return userRepository.findById(id)
                .filter(user -> user.getRoles().contains(roleAdmin))
                .orElseThrow(
                        new NotExistingRecordException("Admin with id " + id + " does not exist!"));
    }

    @Override
    public void editAdmin(UserResource userResource) throws NotExistingRecordException {
        log.debug("Resource {} with new data", userResource);
        final User toEdit = getAdmin(userResource.getId());
        log.debug("Updating entity: {}....", toEdit);
        toEdit.setFirstName(userResource.getFirstName());
        toEdit.setLastName(userResource.getLastName());
        toEdit.setEmail(userResource.getEmail());
        toEdit.setPassword(userResource.getPassword());
        encodePassword(toEdit);
        final User saved = userRepository.save(toEdit);
        log.debug("Entity {} has been updated.", saved);
    }

    @Override
    public List<UserResource> findAllAdmins() {
        return findAllResourcesByRole("ROLE_ADMIN");
    }

    @Override
    public List<UserResource> findAllUsers() {
        return findAllResourcesByRole("ROLE_USER");
    }

    private List<UserResource> findAllResourcesByRole(String roleName) {
        final Role roleAdmin = roleRepository.findFirstByNameIgnoringCase(roleName);
        return userRepository.findAllByRoles(roleAdmin)
                .stream()
                .map(userAssembler::toResource)
                .collect(Collectors.toList());
    }

    private void encodePassword(User user) {
        final String encoded = passwordEncoder.encode(user.getPassword());
        user.setPassword(encoded);
    }
}
