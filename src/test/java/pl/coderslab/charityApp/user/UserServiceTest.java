package pl.coderslab.charityApp.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import pl.coderslab.charityApp.exceptions.NotExistingRecordException;
import pl.coderslab.charityApp.security.Role;
import pl.coderslab.charityApp.security.RoleRepository;
import pl.coderslab.charityApp.user.resources.OrdinaryUserResource;
import pl.coderslab.charityApp.user.resources.ToChangePasswordUserResource;
import pl.coderslab.charityApp.user.resources.ToUpdateUserResource;
import pl.coderslab.charityApp.user.validation.ValidationService;

import javax.validation.ConstraintViolationException;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

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
    @MockBean
    private RoleRepository roleRepositoryMock;
    @MockBean
    private ValidationService validationServiceMock;

    private User adminDb;
    private final String email = "generous@test";
    private OrdinaryUserResource validAdminUserRes;
    private OrdinaryUserResource validUserRes;
    private User userDb;
    private final Role roleAdmin = Role.builder()
            .id(1L)
            .name("ROLE_ADMIN").build();
    private final Role roleUser = Role.builder()
            .id(2L)
            .name("ROLE_USER").build();

    @BeforeEach
    void setUp() {
        adminDb = User.builder()
                .id(1112L)
                .firstName("Jim")
                .lastName("Generous")
                .password("Password2020?")
                .roles(Set.of(roleAdmin))
                .email(email)
                .build();
        userDb = User.builder()
                .id(111332L)
                .firstName("Jack")
                .lastName("Helpful")
                .password("Password2021?")
                .roles(Set.of(roleUser))
                .email("helpful@test")
                .build();
        validAdminUserRes = OrdinaryUserResource.builder()
                .firstName("Jim")
                .lastName("Generous")
                .password("Password2020?")
                .password2("Password2020?")
                .email(email)
                .build();
        validUserRes = OrdinaryUserResource.builder()
                .firstName("Jack")
                .lastName("Helpful")
                .password("Password2021?")
                .password("Password2021?")
                .email("helpful@test")
                .build();
        when(roleRepositoryMock.findFirstByNameIgnoringCase("ROLE_ADMIN"))
                .thenReturn(roleAdmin);
        when(userRepositoryMock.findById(adminDb.getId()))
                .thenReturn(Optional.of(adminDb));
        when(roleRepositoryMock.findFirstByNameIgnoringCase("ROLE_USER"))
                .thenReturn(roleUser);
        when(userRepositoryMock.findById(userDb.getId())).thenReturn(Optional.of(userDb));
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
                .thenReturn(Optional.of(adminDb));

        final User principal = testObject.getPrincipal();

        assertThat(principal, is(adminDb));
    }

    @Test
    @WithMockUser("generous@test")
    void shouldReturnPrincipalResource() throws NotExistingRecordException {
        when(userRepositoryMock.findFirstByEmailIgnoringCase(email))
                .thenReturn(Optional.of(adminDb));
        final OrdinaryUserResource userResource = userAssembler.toResource(adminDb);

        final OrdinaryUserResource principal = testObject.getPrincipalResource();

        assertThat(principal, is(userResource));
    }

    @Test
    @WithMockUser("generous@test")
    void shouldThrowNotExistingRecordExceptionForNotExistingEmail() {
        when(userRepositoryMock.findFirstByEmailIgnoringCase(email))
                .thenReturn(Optional.empty());

        assertThrows(NotExistingRecordException.class,
                () -> testObject.getPrincipal());
    }

    @Test
    void shouldSaveAdminEntity() {
        final String encodedPassword = "%&^%*&^%798798BJHJH";
        when(passwordEncoderMock.encode(validAdminUserRes.getPassword()))
                .thenReturn(encodedPassword);
        when(validationServiceMock.isUniqueEmail(validAdminUserRes.getEmail())).thenReturn(true);
        final User fromResource = userAssembler.fromResource(validAdminUserRes);

        testObject.saveAdmin(validAdminUserRes);

        verify(passwordEncoderMock).encode(validAdminUserRes.getPassword());
        verify(userRepositoryMock).save(fromResource);
    }

    @Test
    void shouldNotSaveNotUniqueAdminEntity() {
        final OrdinaryUserResource notUnique = validAdminUserRes.toBuilder().build();
        when(validationServiceMock.isUniqueEmail(notUnique.getEmail())).thenReturn(false);

        assertThrows(ConstraintViolationException.class,
                () -> testObject.saveAdmin(notUnique));
    }

    @Test
    void shouldSaveUserEntity() {
        final String encodedPassword = "%&^%*&^%798798BJHJH";
        when(validationServiceMock.isUniqueEmail(validUserRes.getEmail())).thenReturn(true);
        when(passwordEncoderMock.encode(validUserRes.getPassword()))
                .thenReturn(encodedPassword);
        final User fromResource = userAssembler.fromResource(validUserRes);

        testObject.saveUser(validUserRes);

        verify(passwordEncoderMock).encode(validUserRes.getPassword());
        verify(userRepositoryMock).save(fromResource);
    }

    @Test
    void shouldNotSaveNotUniqueUserEntity() {
        final OrdinaryUserResource notUnique = validUserRes.toBuilder().build();
        when(validationServiceMock.isUniqueEmail(notUnique.getEmail())).thenReturn(false);

        assertThrows(ConstraintViolationException.class,
                () -> testObject.saveUser(notUnique));
    }

    @Test
    void shouldFindAllResources() {
        final List<User> users = List.of(adminDb, userDb);
        when(userRepositoryMock.findAll()).thenReturn(users);
        final List<OrdinaryUserResource> expected = users.stream()
                .map(userAssembler::toResource)
                .collect(Collectors.toList());

        final List<OrdinaryUserResource> actualUsers = testObject.findAll();

        assertThat(actualUsers, is(expected));
    }

    @Test
    void shouldReturnAdminResource() throws NotExistingRecordException {
        when(userRepositoryMock.findById(adminDb.getId()))
                .thenReturn(Optional.of(adminDb));
        final OrdinaryUserResource expected = userAssembler.toResource(adminDb);

        final OrdinaryUserResource actualAdmin = testObject.getAdminResourceById(adminDb.getId());
        assertThat(actualAdmin, is(expected));
    }

    @Test
    void shouldThrowNotExistingRecordExceptionForOrdinaryUser() {
        when(userRepositoryMock.findById(userDb.getId()))
                .thenReturn(Optional.of(userDb));

        assertThrows(NotExistingRecordException.class,
                () -> testObject.getAdminResourceById(userDb.getId()));
    }

    @Test
    void shouldDeleteAdmin() throws NotExistingRecordException {
        testObject.deleteAdmin(adminDb.getId());

        verify(userRepositoryMock).delete(adminDb);
    }

    @Test
    void shouldEditAdminEntityFromValidResource() throws NotExistingRecordException {
        final Long id = adminDb.getId();
        validAdminUserRes.setId(id);
        final ToUpdateUserResource validToUpdate = userAssembler.toUpdatedResource(validAdminUserRes);
        when(validationServiceMock.isUniqueEmail(validToUpdate.getEmail(), validToUpdate.getId())).thenReturn(true);

        testObject.editAdmin(validToUpdate);

        verify(userRepositoryMock).save(adminDb);
    }

    @Test
    void shouldFindAllAdmins() {
        final List<User> admins = List.of(this.adminDb);
        when(userRepositoryMock.findAllByRoles(roleAdmin)).thenReturn(admins);
        final List<OrdinaryUserResource> expected = admins
                .stream()
                .map(userAssembler::toResource)
                .collect(Collectors.toList());

        final List<OrdinaryUserResource> actual = testObject.findAllAdmins();

        assertThat(actual, is(expected));
    }

    @Test
    void shouldFindAllUsers() {
        final List<User> users = List.of(this.userDb);

        when(userRepositoryMock.findAllByRoles(roleUser)).thenReturn(users);
        final List<OrdinaryUserResource> expected = users
                .stream()
                .map(userAssembler::toResource)
                .collect(Collectors.toList());

        final List<OrdinaryUserResource> actual = testObject.findAllUsers();

        assertThat(actual, is(expected));
    }

    @Test
    void shouldGetUserRecourseByEntityId() throws NotExistingRecordException {
        final OrdinaryUserResource expected = userAssembler.toResource(userDb);

        final OrdinaryUserResource actual = testObject.getUserResourceById(userDb.getId());

        assertThat(actual, is(expected));
    }

    @Test
    void shouldThrowNotExistingRecordExceptionForAdminUser() {
        when(userRepositoryMock.findById(adminDb.getId()))
                .thenReturn(Optional.of(adminDb));

        assertThrows(NotExistingRecordException.class,
                () -> testObject.getUserResourceById(adminDb.getId()));
    }

    @Test
    void shouldDeleteUser() throws NotExistingRecordException {
        testObject.deleteUser(userDb.getId());

        verify(userRepositoryMock).delete(userDb);
    }

    @Test
    void shouldBlockUser() throws NotExistingRecordException {
        final User spyUser = spy(User.class);
        spyUser.setId(userDb.getId());
        spyUser.setRoles(Set.of(roleUser));
        when(userRepositoryMock.findById(userDb.getId())).thenReturn(Optional.of(spyUser));

        testObject.blockUser(userDb.getId());

        verify(spyUser).setEnabled(false);
        verify(userRepositoryMock).save(userDb);
    }

    @Test
    void shouldEditUserEntityFromValidResource() throws NotExistingRecordException {
        final Long id = userDb.getId();
        validUserRes.setId(id);
        final ToUpdateUserResource validToUpdate = userAssembler.toUpdatedResource(validUserRes);
        when(validationServiceMock.isUniqueEmail(validUserRes.getEmail(), validUserRes.getId())).thenReturn(true);

        testObject.editUser(validToUpdate);
        verify(userRepositoryMock).save(userDb);
    }

    @Test
    void shouldChangePasswordForTheSamePasswords() throws NotExistingRecordException {
        final Long id = 2222L;
        final ToUpdateUserResource withNewPass = ToUpdateUserResource.builder()
                .id(id)
                .firstName("Jack")
                .lastName("Helpful")
                .password("Password111?")
                .password2("Password111?")
                .email("helpful@test")
                .build();
        final User toEdit = spy(User.class);
        when(userRepositoryMock.findById(id)).thenReturn(Optional.of(toEdit));

        testObject.changePassword(withNewPass);

        verify(toEdit).setPassword(withNewPass.getPassword());
        verify(passwordEncoderMock).encode(withNewPass.getPassword());
        verify(userRepositoryMock).save(toEdit);
    }

    @Test
    void shouldReturnToUpdateUserResource() throws NotExistingRecordException {
        final ToUpdateUserResource expected = userAssembler.toUpdatedResource(userDb);

        final ToUpdateUserResource actual = testObject.getToUpdateUserResourceById(userDb.getId());

        assertThat(actual, is(expected));
    }

    @Test
    void shouldReturnToUpdateAdminResource() throws NotExistingRecordException {
        final ToUpdateUserResource expected = userAssembler.toUpdatedResource(adminDb);

        final ToUpdateUserResource actual = testObject.getToUpdateAdminResourceById(adminDb.getId());

        assertThat(actual, is(expected));
    }

    @Test
    @WithMockUser("generous@test")
    void shouldReturnToUpdatePrincipalResource() throws NotExistingRecordException {
        when(userRepositoryMock.findFirstByEmailIgnoringCase("generous@test")).thenReturn(Optional.of(adminDb));
        final ToUpdateUserResource expected = userAssembler.toUpdatedResource(adminDb);

        final ToUpdateUserResource actual = testObject.getPrincipalToUpdateResource();

        assertThat(actual, is(expected));
    }

    @Test
    void shouldActivateUser() throws NotExistingRecordException {
        final String uuid = UUID.randomUUID().toString();
        final User toActivate = spy(User.class);
        toActivate.setUuid(uuid);
        toActivate.setEnabled(false);
        when(userRepositoryMock.findFirstByUuid(uuid))
                .thenReturn(Optional.of(toActivate));

        testObject.activate(uuid);

        verify(toActivate).setEnabled(true);
        verify(toActivate).setUuid(null);
        verify(userRepositoryMock).save(toActivate);
    }

    @Test
    void shouldSetUuidToUserEntity() throws NotExistingRecordException {
        final String uuid = UUID.randomUUID().toString();
        final ToChangePasswordUserResource toEdit = userAssembler.toChangePasswordUserResource(userDb);
        userDb.setUuid(uuid);
        when(userRepositoryMock.save(userDb)).thenReturn(userDb);

        testObject.setUuid(toEdit);

        verify(userRepositoryMock).save(userDb);
    }

    @Test
    void shouldChangePassword() throws NotExistingRecordException {
        final String uuid = UUID.randomUUID().toString();
        userDb.setUuid(uuid);
        final ToChangePasswordUserResource toEdit = userAssembler.toChangePasswordUserResource(userDb);
        when(userRepositoryMock.findFirstByUuid(uuid)).thenReturn(Optional.of(userDb));
        userDb.setUuid(null);
        when(userRepositoryMock.save(userDb)).thenReturn(userDb);

        testObject.changePassword(toEdit);

        verify(userRepositoryMock).save(userDb);
    }

    @Test
    void shouldReturnUuidOfUserFromDb() throws NotExistingRecordException {
        final String uuid = UUID.randomUUID().toString();
        userDb.setUuid(uuid);
        when(userRepositoryMock.findById(userDb.getId())).thenReturn(Optional.of(userDb));

        final String actual = testObject.getUuid(userDb.getId());

        assertThat(actual, is(uuid));
    }

    @Test
    void shouldReturnResourceToChangePasswordWithDataByUuid() throws NotExistingRecordException {
        final String uuid = UUID.randomUUID().toString();
        userDb.setUuid(uuid);
        final ToChangePasswordUserResource expected = userAssembler.toChangePasswordUserResource(userDb);
        when(userRepositoryMock.findFirstByUuid(uuid)).thenReturn(Optional.of(userDb));

        final ToChangePasswordUserResource actual = testObject.getUserToChangePasswordByUuid(uuid);

        assertThat(actual, is(expected));
    }

    @Test
    void shouldReturnResourceToChangePasswordWithDataByEmail() throws NotExistingRecordException {
        final String email = userDb.getEmail();
        final ToChangePasswordUserResource expected = userAssembler.toChangePasswordUserResource(userDb);
        when(userRepositoryMock.findFirstByEmailIgnoringCase(email)).thenReturn(Optional.of(userDb));

        final ToChangePasswordUserResource actual = testObject.findByEmail(email);

        assertThat(actual, is(expected));
    }
}