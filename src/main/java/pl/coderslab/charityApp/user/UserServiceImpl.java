package pl.coderslab.charityApp.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import pl.coderslab.charityApp.exceptions.NotExistingRecordException;
import pl.coderslab.charityApp.security.Role;
import pl.coderslab.charityApp.security.RoleRepository;
import pl.coderslab.charityApp.user.resources.OrdinaryUserResource;
import pl.coderslab.charityApp.user.resources.ToChangePasswordUserResource;
import pl.coderslab.charityApp.user.resources.ToUpdateUserResource;
import pl.coderslab.charityApp.user.validation.group.ChangePassword;
import pl.coderslab.charityApp.user.validation.group.PreChecked;
import pl.coderslab.charityApp.user.validation.group.PreCheckedUpdating;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;
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
    public void saveUser(@Valid OrdinaryUserResource userResource) {
        final User user = getUser(userResource, "ROLE_USER");
        setUuid(user);
        save(user);
        userResource.setId(user.getId());
    }

    private void setUuid(User user) {
        final String uuid = UUID.randomUUID().toString();
        user.setUuid(uuid);
    }

    @Override
    @Transactional
    @Validated(PreChecked.class)
    public void saveAdmin(@Valid OrdinaryUserResource userResource) {
        final User user = getUser(userResource, "ROLE_ADMIN");
        save(user);
    }

    @Override
    public String getUuid(Long id) throws NotExistingRecordException {
        return getUser(id).getUuid();
    }

    private User getUser(OrdinaryUserResource userResource, String role_user) {
        final User user = userAssembler.fromResource(userResource);
        user.addRole(roleRepository.findFirstByNameIgnoringCase(role_user));
        return user;
    }

    private User save(User user) {
        log.debug("Preparing to save entity: {}...", user);
        encodePassword(user);
        final User saved = userRepository.save(user);
        log.debug("Entity {} has been saved", saved);
        return saved;
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
    public OrdinaryUserResource getPrincipalResource() throws NotExistingRecordException {
        final User principal = getPrincipal();
        return userAssembler.toResource(principal);
    }

    @Override
    public List<OrdinaryUserResource> findAll() {
        return userRepository
                .findAll()
                .stream()
                .map(userAssembler::toResource)
                .collect(Collectors.toList());
    }

    @Override
    public OrdinaryUserResource getAdminResourceById(Long id) throws NotExistingRecordException {
        final Role roleAdmin = roleRepository.findFirstByNameIgnoringCase("ROLE_ADMIN");
        return userRepository.findById(id)
                .filter(user -> user.getRoles().contains(roleAdmin))
                .map(userAssembler::toResource)
                .orElseThrow(
                        new NotExistingRecordException("Admin with id " + id + " does not exist!"));
    }

    @Override
    public OrdinaryUserResource getUserResourceById(Long id) throws NotExistingRecordException {
        final Role roleUser = roleRepository.findFirstByNameIgnoringCase("ROLE_USER");
        return userRepository.findById(id)
                .filter(user -> user.getRoles().contains(roleUser))
                .map(userAssembler::toResource)
                .orElseThrow(
                        new NotExistingRecordException("User with id " + id + " does not exist!"));
    }

    @Override
    public ToUpdateUserResource getToUpdateUserResourceById(Long id) throws NotExistingRecordException {
        return userAssembler.toUpdatedResource(getUserResourceById(id));
    }

    @Override
    public ToUpdateUserResource getToUpdateAdminResourceById(Long id) throws NotExistingRecordException {
        return userAssembler.toUpdatedResource(getAdminResourceById(id));
    }

    @Override
    public void activate(String uuid) throws NotExistingRecordException {
        log.debug("Preparing to update the entity with uuid {}...", uuid);
        final User toActivate = getUserByUuid(uuid);
        log.debug("Preparing to update the entity {}...", toActivate);
        toActivate.setEnabled(true);
        toActivate.setUuid(null);
        final User activated = userRepository.save(toActivate);
        log.debug("Entity {} has been updated ", activated);
    }

    private User getUserByUuid(String uuid) throws NotExistingRecordException {
        return userRepository.findFirstByUuid(uuid).orElseThrow(
                new NotExistingRecordException("User with uuid " + uuid + " does not exist!"));
    }

    @Override
    public ToChangePasswordUserResource getUserToChangePasswordByUuid(String uuid) throws NotExistingRecordException {
        return userAssembler.toChangePasswordUserResource(getUserByUuid(uuid));
    }

    @Override
    public ToChangePasswordUserResource findByEmail(String email) throws NotExistingRecordException {
        return userRepository.findFirstByEmailIgnoringCase(email).map(userAssembler::toChangePasswordUserResource)
                .orElseThrow(
                        new NotExistingRecordException("User with email " + email + " does not exist!"));
    }

    @Override
    @Transactional
    public void setUuid(ToChangePasswordUserResource userResource) throws NotExistingRecordException {
        final User toEdit = getUser(userResource.getId());
        log.debug("Updating entity: {}....", toEdit);
        setUuid(toEdit);
        final User edited = userRepository.save(toEdit);
        log.debug("Entity {} has been updated.", edited);
        userResource.setUuid(edited.getUuid());
    }

    @Override
    public ToUpdateUserResource getPrincipalToUpdateResource() throws NotExistingRecordException {
        return userAssembler.toUpdatedResource(getPrincipalResource());
    }

    @Override
    @Transactional
    public void deleteAdmin(Long id) throws NotExistingRecordException {
        log.debug("Preparing to delete entity with id {}...", id);
        final User toDelete = getAdmin(id);
        delete(toDelete);
    }

    @Override
    @Transactional
    public void deleteUser(Long id) throws NotExistingRecordException {
        log.debug("Preparing to delete entity with id {}...", id);
        final User toDelete = getUser(id);
        delete(toDelete);
    }

    @Override
    @Transactional
    public void blockUser(Long id) throws NotExistingRecordException {
        log.debug("Preparing to block entity with id {}...", id);
        final User toBlock = getUser(id);
        toBlock.setEnabled(false);
        final User blocked = userRepository.save(toBlock);
        log.debug("Entity {} has been blocked.", blocked);
    }

    private void delete(User toDelete) {
        log.debug("Deleting entity: {}....", toDelete);
        userRepository.delete(toDelete);
        log.debug("Entity {} has been deleted.", toDelete);
    }

    @Transactional
    @Override
    @Validated(PreCheckedUpdating.class)
    public void editAdmin(@Valid ToUpdateUserResource userResource) throws NotExistingRecordException {
        log.debug("Resource {} with new data", userResource);
        final User toEdit = getAdmin(userResource.getId());
        edit(userResource, toEdit);
    }

    @Transactional
    @Override
    @Validated(ChangePassword.class)
    public void changePassword(@Valid ToUpdateUserResource userResource) throws NotExistingRecordException {
        final User toEdit = getUserOrAdmin(userResource.getId());
        updatePassword(toEdit, userResource.getPassword2());
    }

    @Transactional
    @Override
    public void changePassword(ToChangePasswordUserResource userResource) throws NotExistingRecordException {
        final String uuid = userResource.getUuid();
        final User toEdit = getUserByUuid(uuid);
        toEdit.setUuid(null);
        updatePassword(toEdit, userResource.getPassword2());
    }

    private void updatePassword(User toEdit, String password2) throws NotExistingRecordException {
        if (password2 != null) {
            toEdit.setPassword(password2);
            encodePassword(toEdit);
        }
        final User edited = userRepository.save(toEdit);
        log.debug("Entity {} password has been updated.", edited);
    }

    @Transactional
    @Override
    @Validated(PreCheckedUpdating.class)
    public void editUser(@Valid ToUpdateUserResource userResource) throws NotExistingRecordException {
        log.debug("Resource {} with new data", userResource);
        final User toEdit = getUser(userResource.getId());
        edit(userResource, toEdit);
    }

    private void edit(ToUpdateUserResource userResource, User toEdit) {
        log.debug("Updating entity: {}....", toEdit);
        toEdit.setFirstName(userResource.getFirstName());
        toEdit.setLastName(userResource.getLastName());
        toEdit.setEmail(userResource.getEmail());
        final User saved = userRepository.save(toEdit);
        log.debug("Entity {} has been updated.", saved);
    }

    @Override
    public List<OrdinaryUserResource> findAllAdmins() {
        return findAllResourcesByRole("ROLE_ADMIN");
    }

    @Override
    public List<OrdinaryUserResource> findAllUsers() {
        return findAllResourcesByRole("ROLE_USER");
    }

    private List<OrdinaryUserResource> findAllResourcesByRole(String roleName) {
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

    private User getUserOrAdmin(Long id) throws NotExistingRecordException {
        return userRepository.findById(id)
                .orElseThrow(
                        new NotExistingRecordException("User with id " + id + " does not exist!"));
    }

    private User getAdmin(Long id) throws NotExistingRecordException {
        final Role roleAdmin = roleRepository.findFirstByNameIgnoringCase("ROLE_ADMIN");
        return userRepository.findById(id)
                .filter(user -> user.getRoles().contains(roleAdmin))
                .orElseThrow(
                        new NotExistingRecordException("Admin with id " + id + " does not exist!"));
    }

    private User getUser(Long id) throws NotExistingRecordException {
        final Role roleUser = roleRepository.findFirstByNameIgnoringCase("ROLE_USER");
        return userRepository.findById(id)
                .filter(user -> user.getRoles().contains(roleUser))
                .orElseThrow(
                        new NotExistingRecordException("User with id " + id + " does not exist!"));
    }
}
