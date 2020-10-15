package pl.coderslab.charityApp.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import pl.coderslab.charityApp.exceptions.NotExistingRecordException;

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


@WebMvcTest(UsersController.class)
@ActiveProfiles("test")
@WithMockUser(roles = "USER", username = "user@test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UsersControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private UserService userServiceMock;
    private UserResource userRes;
    private UserResource anotherUserRes;

    @BeforeEach
    void setUp() throws NotExistingRecordException {
        final String email = "user@test";
        userRes = UserResource.builder()
                .id(1112L)
                .firstName("Jim")
                .lastName("Generous")
                .password("Password2020?")
                .password2("Password2020?")
                .email(email)
                .enabled(true)
                .build();
        anotherUserRes = UserResource.builder()
                .id(111332L)
                .firstName("Jack")
                .lastName("Helpful")
                .password("Password2021?")
                .password2("Password2021?")
                .email("helpful@test")
                .enabled(true)
                .build();
        when(userServiceMock.getPrincipalResource()).thenReturn(userRes);
    }

    @Test
    void shouldPrepareListPage() throws Exception {
        final List<UserResource> users = List.of(userRes, anotherUserRes);
        when(userServiceMock.findAllUsers()).thenReturn(users);
        mockMvc.perform(get("/app/admin/users"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(model().attribute("users", users))
                .andExpect(view().name("/admin/users/list"));
        verify(userServiceMock).findAllUsers();
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
        when(userServiceMock.getUserResourceById(id)).thenReturn(toEdit);

        mockMvc.perform(get("/app/admin/users/edit")
                .param("id", id.toString()))
                .andExpect(status().is2xxSuccessful())
                .andExpect(model().attribute("user", toEdit))
                .andExpect(view().name("/admin/users/edit"));
        verify(userServiceMock).getUserResourceById(id);
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

        mockMvc.perform(post("/app/admin/users/edit").with(csrf())
                .flashAttr("user", resourceWithNewData))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/app/admin/users"));
        verify(userServiceMock).editUser(resourceWithNewData);
    }

    @Test
    void shouldNotUpdateFromInvalidResource() throws Exception {
        final UserResource invalidResource = new UserResource();

        mockMvc.perform(post("/app/admin/users/edit").with(csrf())
                .flashAttr("user", invalidResource))
                .andExpect(status().is2xxSuccessful())
                .andExpect(model().errorCount(5))
                .andExpect(model().attributeHasFieldErrors(
                        "user", "firstName", "lastName", "email", "password", "password2"))
                .andExpect(view().name("/admin/users/edit"));
        verify(userServiceMock, atMost(0)).editUser(invalidResource);
    }

    @Test
    void shouldNotEditEntityWithNotUniqueUserEmail() throws Exception {
        final UserResource duplicateUser = userRes.toBuilder().email("generous@test").build();
        final ConstraintViolation<String> violation = mock(ConstraintViolation.class);
        final Path mockPath = mock(Path.class);
        final Path.Node nodeMock = mock(Path.Node.class);
        final Iterator<Path.Node> iteratorMock = mock(Iterator.class);
        when(violation.getPropertyPath()).thenReturn(mockPath);
        when(mockPath.iterator()).thenReturn(iteratorMock);
        when(iteratorMock.next()).thenReturn(nodeMock);
        final Set<ConstraintViolation<String>> violations = Set.of(violation);
        doThrow(new ConstraintViolationException(violations))
                .when(userServiceMock).editUser(duplicateUser);

        mockMvc.perform(post("/app/admin/users/edit").with(csrf())
                .flashAttr("user", duplicateUser))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("/admin/users/edit"));
        verify(userServiceMock).editUser(duplicateUser);
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

        when(userServiceMock.getPrincipalResource()).thenReturn(userRes);
        when(userServiceMock.getUserResourceById(id)).thenReturn(toDelete);

        mockMvc.perform(get("/app/admin/users/delete")
                .param("id", id.toString()))
                .andExpect(status().is2xxSuccessful())
                .andExpect(model().attribute("user", toDelete))
                .andExpect(view().name("/admin/users/delete"));
        verify(userServiceMock).getUserResourceById(id);
    }

    @Test
    void shouldNotPrepareDeletePageAndReturnToList() throws Exception {
        final Long id = userRes.getId();

        when(userServiceMock.getPrincipalResource()).thenReturn(userRes);
        when(userServiceMock.getUserResourceById(id)).thenReturn(userRes);

        mockMvc.perform(get("/app/admin/users/delete")
                .param("id", id.toString()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/app/admin/users"));
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
        when(userServiceMock.getPrincipalResource()).thenReturn(userRes);
        when(userServiceMock.getUserResourceById(id)).thenReturn(toDelete);

        mockMvc.perform(post("/app/admin/users/delete").with(csrf())
                .param("id", id.toString()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/app/admin/users"));
        verify(userServiceMock).deleteUser(id);
    }

    @Test
    void shouldNotDeleteAndReturnToList() throws Exception {
        final Long id = userRes.getId();

        when(userServiceMock.getPrincipalResource()).thenReturn(userRes);
        when(userServiceMock.getUserResourceById(id)).thenReturn(userRes);

        mockMvc.perform(post("/app/admin/users/delete").with(csrf())
                .param("id", id.toString()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/app/admin/users"));
    }

    @Test
    void shouldPrepareBlockPage() throws Exception {
        final Long id = 2222L;
        final UserResource toBlock = UserResource.builder()
                .id(id)
                .firstName("Jack")
                .lastName("Helpful")
                .password("Password2021?")
                .password2("Password2021?")
                .email("helpful@test")
                .build();

        when(userServiceMock.getPrincipalResource()).thenReturn(userRes);
        when(userServiceMock.getUserResourceById(id)).thenReturn(toBlock);

        mockMvc.perform(get("/app/admin/users/block")
                .param("id", id.toString()))
                .andExpect(status().is2xxSuccessful())
                .andExpect(model().attribute("user", toBlock))
                .andExpect(view().name("/admin/users/block"));
        verify(userServiceMock).getUserResourceById(id);
    }

    @Test
    void shouldNotPrepareBlockPageAndReturnToList() throws Exception {
        final Long id = userRes.getId();

        when(userServiceMock.getPrincipalResource()).thenReturn(userRes);
        when(userServiceMock.getUserResourceById(id)).thenReturn(userRes);

        mockMvc.perform(get("/app/admin/users/block")
                .param("id", id.toString()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/app/admin/users"));
    }

    @Test
    void shouldBlockEntityById() throws Exception {
        final Long id = 2222L;
        final UserResource toBlock = UserResource.builder()
                .id(id)
                .firstName("Jack")
                .lastName("Helpful")
                .password("Password2021?")
                .password2("Password2021?")
                .email("helpful@test")
                .build();
        when(userServiceMock.getPrincipalResource()).thenReturn(userRes);
        when(userServiceMock.getUserResourceById(id)).thenReturn(toBlock);

        mockMvc.perform(post("/app/admin/users/block").with(csrf())
                .param("id", id.toString()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/app/admin/users"));
        verify(userServiceMock).blockUser(id);
    }

    @Test
    void shouldNotBlockAndReturnToList() throws Exception {
        final Long id = userRes.getId();

        when(userServiceMock.getPrincipalResource()).thenReturn(userRes);
        when(userServiceMock.getUserResourceById(id)).thenReturn(userRes);

        mockMvc.perform(post("/app/admin/users/block").with(csrf())
                .param("id", id.toString()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/app/admin/users"));
    }
}