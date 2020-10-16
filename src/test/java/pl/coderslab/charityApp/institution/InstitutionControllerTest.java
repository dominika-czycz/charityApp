package pl.coderslab.charityApp.institution;

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
import pl.coderslab.charityApp.user.OrdinaryUserResource;
import pl.coderslab.charityApp.user.UserService;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(InstitutionController.class)
@ActiveProfiles("test")
@WithMockUser(roles = "ADMIN", username = "admin@test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class InstitutionControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private InstitutionService institutionServiceMock;
    @MockBean
    UserService userServiceMock;

    @BeforeEach
    void setUp() throws NotExistingRecordException {
        final String email = "admin@test";
        final OrdinaryUserResource userResource = OrdinaryUserResource.builder()
                .id(1112L)
                .firstName("Jim")
                .lastName("Generous")
                .password("Password2020?")
                .password2("Password2020?")
                .email(email)
                .build();
        when(userServiceMock.getPrincipalResource()).thenReturn(userResource);
    }

    @Test
    void shouldPrepareListPage() throws Exception {
        final InstitutionResource institution = InstitutionResource.builder().id(10L).name("All children").build();
        final InstitutionResource institution1 = InstitutionResource.builder().id(23L).name("Animals").build();
        final List<InstitutionResource> institutions = List.of(institution, institution1);
        when(institutionServiceMock.findAll()).thenReturn(institutions);
        mockMvc.perform(get("/app/admin/institution"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("institutions", institutions))
                .andExpect(view().name("/admin/institution/list"));
        verify(institutionServiceMock).findAll();
    }

    @Test
    void shouldPrepareAddPage() throws Exception {
        mockMvc.perform(get("/app/admin/institution/add"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(model().attribute("institutionResource", new InstitutionResource()))
                .andExpect(view().name("/admin/institution/add"));
    }

    @Test
    void shouldSaveEntityFromValidResource() throws Exception {
        final InstitutionResource validResource = InstitutionResource
                .builder()
                .name("All children")
                .description("We help all children...")
                .build();

        mockMvc.perform(post("/app/admin/institution/add").with(csrf())
                .flashAttr("institutionResource", validResource))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/app/admin/institution"));
        verify(institutionServiceMock).save(validResource);
    }

    @Test
    void shouldNotSaveInvalidResource() throws Exception {
        final InstitutionResource invalidResource = new InstitutionResource();

        mockMvc.perform(post("/app/admin/institution/add").with(csrf())
                .flashAttr("institutionResource", invalidResource))
                .andExpect(status().is2xxSuccessful())
                .andExpect(model().errorCount(2))
                .andExpect(model().attributeHasFieldErrors("institutionResource", "name", "description"))
                .andExpect(view().name("/admin/institution/add"));
        verify(institutionServiceMock, atMost(0)).save(invalidResource);
    }

    @Test
    void shouldPrepareEditPage() throws Exception {
        final Long id = 2222L;
        final InstitutionResource toEdit = InstitutionResource
                .builder()
                .id(id)
                .name("All children")
                .description("We help all children...")
                .build();
        when(institutionServiceMock.getResourceById(id)).thenReturn(toEdit);

        mockMvc.perform(get("/app/admin/institution/edit")
                .param("id", id.toString()))
                .andExpect(status().is2xxSuccessful())
                .andExpect(model().attribute("institutionResource", toEdit))
                .andExpect(view().name("/admin/institution/edit"));
        verify(institutionServiceMock).getResourceById(id);
    }

    @Test
    void shouldUpdateEntityFromValidResource() throws Exception {
        final Long id = 2222L;
        final InstitutionResource resourceWithNewData = InstitutionResource
                .builder()
                .id(id)
                .name("All children")
                .description("We help all children...")
                .build();

        mockMvc.perform(post("/app/admin/institution/edit").with(csrf())
                .flashAttr("institutionResource", resourceWithNewData))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/app/admin/institution"));
        verify(institutionServiceMock).edit(resourceWithNewData);
    }

    @Test
    void shouldNotUpdateFromInvalidResource() throws Exception {
        final InstitutionResource invalidResource = new InstitutionResource();

        mockMvc.perform(post("/app/admin/institution/edit").with(csrf())
                .flashAttr("institutionResource", invalidResource))
                .andExpect(status().is2xxSuccessful())
                .andExpect(model().errorCount(2))
                .andExpect(model().attributeHasFieldErrors("institutionResource", "name", "description"))
                .andExpect(view().name("/admin/institution/edit"));
        verify(institutionServiceMock, atMost(0)).edit(invalidResource);
    }
    @Test
    void shouldPrepareDeletePage() throws Exception {
        final Long id = 2222L;
        final InstitutionResource toDelete = InstitutionResource
                .builder()
                .id(id)
                .name("All children")
                .description("We help all children...")
                .build();
        when(institutionServiceMock.getResourceById(id)).thenReturn(toDelete);

        mockMvc.perform(get("/app/admin/institution/delete")
                .param("id", id.toString()))
                .andExpect(status().is2xxSuccessful())
                .andExpect(model().attribute("institutionResource", toDelete))
                .andExpect(view().name("/admin/institution/delete"));
        verify(institutionServiceMock).getResourceById(id);
    }

    @Test
    void shouldDeleteEntityById() throws Exception {
        final Long id = 2222L;

        mockMvc.perform(post("/app/admin/institution/delete").with(csrf())
                .param("id", id.toString()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/app/admin/institution"));
        verify(institutionServiceMock).delete(id);
    }

}