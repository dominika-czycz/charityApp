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
import pl.coderslab.charityApp.user.validation.ValidationService;

import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.Optional;
import java.util.Set;
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
    private UserResource validAdminUserRes;
    private UserResource validUserRes;
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
        validAdminUserRes = UserResource.builder()
                .firstName("Jim")
                .lastName("Generous")
                .password("Password2020?")
                .password2("Password2020?")
                .email(email)
                .build();
        validUserRes = UserResource.builder()
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
        final UserResource userResource = userAssembler.toResource(adminDb);

        final UserResource principal = testObject.getPrincipalResource();

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
        final UserResource notUnique = validAdminUserRes.toBuilder().build();
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
        final UserResource notUnique = validUserRes.toBuilder().build();
        when(validationServiceMock.isUniqueEmail(notUnique.getEmail())).thenReturn(false);

        assertThrows(ConstraintViolationException.class,
                () -> testObject.saveUser(notUnique));
    }

    @Test
    void shouldFindAllResources() {
        final List<User> users = List.of(adminDb, userDb);
        when(userRepositoryMock.findAll()).thenReturn(users);
        final List<UserResource> expected = users.stream()
                .map(userAssembler::toResource)
                .collect(Collectors.toList());

        final List<UserResource> actualUsers = testObject.findAll();

        assertThat(actualUsers, is(expected));
    }

    @Test
    void shouldReturnAdminResource() throws NotExistingRecordException {
        when(userRepositoryMock.findById(adminDb.getId()))
                .thenReturn(Optional.of(adminDb));
        final UserResource expected = userAssembler.toResource(adminDb);

        final UserResource actualAdmin = testObject.getAdminResourceById(adminDb.getId());
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
        final String encodedPassword = "%&^%*&^%798798BJHJH";
        final Long id = adminDb.getId();
        validAdminUserRes.setId(id);
        when(validationServiceMock.isUniqueEmail(validAdminUserRes.getEmail(), validAdminUserRes.getId())).thenReturn(true);
        when(passwordEncoderMock.encode(validAdminUserRes.getPassword()))
                .thenReturn(encodedPassword);

        testObject.editAdmin(validAdminUserRes);
        verify(passwordEncoderMock).encode(validAdminUserRes.getPassword());
        verify(userRepositoryMock).save(adminDb);
    }

    @Test
    void shouldFindAllAdmins() {
        final List<User> admins = List.of(this.adminDb);
        when(userRepositoryMock.findAllByRoles(roleAdmin)).thenReturn(admins);
        final List<UserResource> expected = admins
                .stream()
                .map(userAssembler::toResource)
                .collect(Collectors.toList());

        final List<UserResource> actual = testObject.findAllAdmins();

        assertThat(actual, is(expected));
    }

    @Test
    void shouldFindAllUsers() {
        final List<User> users = List.of(this.userDb);

        when(userRepositoryMock.findAllByRoles(roleUser)).thenReturn(users);
        final List<UserResource> expected = users
                .stream()
                .map(userAssembler::toResource)
                .collect(Collectors.toList());

        final List<UserResource> actual = testObject.findAllUsers();

        assertThat(actual, is(expected));
    }

    @Test
    void shouldGetUserRecourseByEntityId() throws NotExistingRecordException {
        final UserResource expected = userAssembler.toResource(userDb);

        final UserResource actual = testObject.getUserResourceById(userDb.getId());

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
        final String encodedPassword = "%&^%*&^%798798BJHJH";
        final Long id = userDb.getId();
        validUserRes.setId(id);
        when(validationServiceMock.isUniqueEmail(validUserRes.getEmail(), validUserRes.getId())).thenReturn(true);
        when(passwordEncoderMock.encode(validUserRes.getPassword()))
                .thenReturn(encodedPassword);

        testObject.editUser(validUserRes);
        verify(passwordEncoderMock).encode(validUserRes.getPassword());
        verify(userRepositoryMock).save(userDb);
    }
}