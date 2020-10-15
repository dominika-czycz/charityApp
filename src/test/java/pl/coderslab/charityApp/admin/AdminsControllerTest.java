package pl.coderslab.charityApp.admin;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import pl.coderslab.charityApp.email.EmailService;
import pl.coderslab.charityApp.exceptions.NotExistingRecordException;
import pl.coderslab.charityApp.user.UserResource;
import pl.coderslab.charityApp.user.UserService;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Path;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(AdminsController.class)
@ActiveProfiles("test")
@WithMockUser(roles = "ADMIN", username = "admin@test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class AdminsControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private UserService userServiceMock;
    @MockBean
    private EmailService emailServiceMock;
    private UserResource adminRes;
    private UserResource anotherAdminRes;

    @BeforeEach
    void setUp() throws NotExistingRecordException {
        final String email = "admin@test";
        adminRes = UserResource.builder()
                .id(1112L)
                .firstName("Jim")
                .lastName("Generous")
                .password("Password2020?")
                .password2("Password2020?")
                .email(email)
                .build();
        anotherAdminRes = UserResource.builder()
                .id(111332L)
                .firstName("Jack")
                .lastName("Helpful")
                .password("Password2021?")
                .password2("Password2021?")
                .email("helpful@test")
                .build();
        when(userServiceMock.getPrincipalResource()).thenReturn(adminRes);
    }

    @Test
    void shouldPrepareListPage() throws Exception {
        final List<UserResource> admins = List.of(adminRes, anotherAdminRes);
        when(userServiceMock.findAllAdmins()).thenReturn(admins);
        mockMvc.perform(get("/app/admin/admins"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(model().attribute("admins", admins))
                .andExpect(view().name("/admin/admins/list"));
        verify(userServiceMock).findAllAdmins();
    }

    @Test
    void shouldPrepareAddPage() throws Exception {
        mockMvc.perform(get("/app/admin/admins/add"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(model().attribute("admin", new UserResource()))
                .andExpect(view().name("/admin/admins/add"));
    }

    @Test
    void shouldSaveEntityFromValidResource() throws Exception {
        final UserResource validResource = UserResource.builder()
                .firstName("Jack")
                .lastName("Helpful")
                .password("Password2021?")
                .password2("Password2021?")
                .email("helpful@test")
                .build();
        mockMvc.perform(post("/app/admin/admins/add").with(csrf())
                .flashAttr("admin", validResource))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/app/admin/admins"));
        verify(userServiceMock).saveAdmin(validResource);
        verify(emailServiceMock).sendRegistrationConfirmation(validResource);
    }

    @Test
    void shouldNotSaveInvalidResource() throws Exception {
        final UserResource invalidResource = new UserResource();

        mockMvc.perform(post("/app/admin/admins/add").with(csrf())
                .flashAttr("admin", invalidResource))
                .andExpect(status().is2xxSuccessful())
                .andExpect(model().errorCount(5))
                .andExpect(model().attributeHasFieldErrors(
                        "admin", "firstName", "lastName", "email", "password", "password2"))
                .andExpect(view().name("/admin/admins/add"));
        verify(userServiceMock, atMost(0)).saveAdmin(invalidResource);
        verify(emailServiceMock, atMost(0)).sendRegistrationConfirmation(invalidResource);
    }

    @Test
    void shouldNotSaveNotUniqueUser() throws Exception {
        final UserResource duplicateUser = adminRes.toBuilder().email("generous@test").build();
        final ConstraintViolation<String> violation = mock(ConstraintViolation.class);
        final Path mockPath = mock(Path.class);
        final Path.Node nodeMock = mock(Path.Node.class);
        final Iterator<Path.Node> iteratorMock = mock(Iterator.class);
        when(violation.getPropertyPath()).thenReturn(mockPath);
        when(mockPath.iterator()).thenReturn(iteratorMock);
        when(iteratorMock.next()).thenReturn(nodeMock);
        final Set<ConstraintViolation<String>> violations = Set.of(violation);
        doThrow(new ConstraintViolationException(violations))
                .when(userServiceMock).saveAdmin(duplicateUser);

        mockMvc.perform(post("/app/admin/admins/add").with(csrf())
                .flashAttr("admin", duplicateUser))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("/admin/admins/add"));
        verify(userServiceMock).saveAdmin(duplicateUser);
    }

    @Test
    void shouldPrepareEditPage() throws Exception {
        final Long id = 2222L;
        final UserResource toEdit = UserResource.builder()
                .id(id)
                .firstName("Jack")
                .lastName("Helpful")
                .password("Password2021?")
                .password2("Password2021?")
                .email("helpful@test")
                .build();
        when(userServiceMock.getAdminResourceById(id)).thenReturn(toEdit);

        mockMvc.perform(get("/app/admin/admins/edit")
                .param("id", id.toString()))
                .andExpect(status().is2xxSuccessful())
                .andExpect(model().attribute("admin", toEdit))
                .andExpect(view().name("/admin/admins/edit"));
        verify(userServiceMock).getAdminResourceById(id);
    }

    @Test
    void shouldUpdateEntityFromValidResource() throws Exception {
        final Long id = 2222L;
        final UserResource resourceWithNewData = UserResource.builder()
                .id(id)
                .firstName("Jack")
                .lastName("Helpful")
                .password("Password2021?")
                .password2("Password2021?")
                .email("helpful@test")
                .build();

        mockMvc.perform(post("/app/admin/admins/edit").with(csrf())
                .flashAttr("admin", resourceWithNewData))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/app/admin/admins"));
        verify(userServiceMock).editAdmin(resourceWithNewData);
    }

    @Test
    void shouldNotUpdateFromInvalidResource() throws Exception {
        final UserResource invalidResource = new UserResource();

        mockMvc.perform(post("/app/admin/admins/edit").with(csrf())
                .flashAttr("admins", invalidResource))
                .andExpect(status().is2xxSuccessful())
                .andExpect(model().errorCount(5))
                .andExpect(model().attributeHasFieldErrors(
                        "admin", "firstName", "lastName", "email", "password", "password2"))
                .andExpect(view().name("/admin/admins/edit"));
        verify(userServiceMock, atMost(0)).editAdmin(invalidResource);
    }

    @Test
    void shouldPrepareDeletePage() throws Exception {
        final Long id = 2222L;
        final UserResource toDelete = UserResource.builder()
                .id(id)
                .firstName("Jack")
                .lastName("Helpful")
                .password("Password2021?")
                .password2("Password2021?")
                .email("helpful@test")
                .build();

        when(userServiceMock.getPrincipalResource()).thenReturn(adminRes);
        when(userServiceMock.getAdminResourceById(id)).thenReturn(toDelete);

        mockMvc.perform(get("/app/admin/admins/delete")
                .param("id", id.toString()))
                .andExpect(status().is2xxSuccessful())
                .andExpect(model().attribute("admin", toDelete))
                .andExpect(view().name("/admin/admins/delete"));
        verify(userServiceMock).getAdminResourceById(id);
    }

    @Test
    void shouldNotPrepareDeletePageAndReturnToList() throws Exception {
        final Long id = adminRes.getId();

        when(userServiceMock.getPrincipalResource()).thenReturn(adminRes);
        when(userServiceMock.getAdminResourceById(id)).thenReturn(adminRes);

        mockMvc.perform(get("/app/admin/admins/delete")
                .param("id", id.toString()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/app/admin/admins"));
    }

    @Test
    void shouldDeleteEntityById() throws Exception {
        final Long id = 2222L;
        final UserResource toDelete = UserResource.builder()
                .id(id)
                .firstName("Jack")
                .lastName("Helpful")
                .password("Password2021?")
                .password2("Password2021?")
                .email("helpful@test")
                .build();
        when(userServiceMock.getPrincipalResource()).thenReturn(adminRes);
        when(userServiceMock.getAdminResourceById(id)).thenReturn(toDelete);

        mockMvc.perform(post("/app/admin/admins/delete").with(csrf())
                .param("id", id.toString()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/app/admin/admins"));
        verify(userServiceMock).deleteAdmin(id);
    }

    @Test
    void shouldNotDeleteAndReturnToList() throws Exception {
        final Long id = adminRes.getId();

        when(userServiceMock.getPrincipalResource()).thenReturn(adminRes);
        when(userServiceMock.getAdminResourceById(id)).thenReturn(adminRes);

        mockMvc.perform(post("/app/admin/admins/delete").with(csrf())
                .param("id", id.toString()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/app/admin/admins"));
    }
}