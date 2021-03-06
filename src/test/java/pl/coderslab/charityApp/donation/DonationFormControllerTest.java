package pl.coderslab.charityApp.donation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import pl.coderslab.charityApp.category.Category;
import pl.coderslab.charityApp.category.CategoryService;
import pl.coderslab.charityApp.donation.controllers.DonationFormController;
import pl.coderslab.charityApp.donation.resources.DonationResource;
import pl.coderslab.charityApp.email.EmailService;
import pl.coderslab.charityApp.exceptions.NotExistingRecordException;
import pl.coderslab.charityApp.institution.Institution;
import pl.coderslab.charityApp.institution.InstitutionResource;
import pl.coderslab.charityApp.institution.InstitutionService;
import pl.coderslab.charityApp.user.resources.OrdinaryUserResource;
import pl.coderslab.charityApp.user.UserService;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DonationFormController.class)
@ActiveProfiles("test")
@WithMockUser
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class DonationFormControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private DonationService donationServiceMock;
    @MockBean
    private CategoryService categoryServiceMock;
    @MockBean
    private UserService userServiceMock;
    @MockBean
    private InstitutionService institutionServiceMock;
    @MockBean
    private EmailService emailServiceMock;
    private DonationResource donation;


    @BeforeEach
    void setUp() throws NotExistingRecordException {
        final Institution institution1 = Institution.builder().id(23L).name("Animals").build();
        final Category toys = Category.builder().id(11L).name("toys").build();
        final Category books = Category.builder().id(122L).name("books").build();
        final Set<Category> categories = Set.of(toys, books);
        donation = DonationResource.builder()
                .categories(categories)
                .institution(institution1)
                .city("Wrocław")
                .phoneNumber("+48 404 404 404")
                .pickUpDate(LocalDate.now().plusMonths(1))
                .pickUpTime(LocalTime.now())
                .street("Wrocławska")
                .quantity(2)
                .zipCode("34-333")
                .build();
        final OrdinaryUserResource userResource = OrdinaryUserResource.builder()
                .id(1112L)
                .firstName("Jim")
                .lastName("Generous")
                .password("Password2020?")
                .email("test@email")
                .build();
        when(userServiceMock.getPrincipalResource()).thenReturn(userResource);
    }

    @Test
    void shouldDisplayDonationForm() throws Exception {
        final InstitutionResource institution = InstitutionResource.builder().id(10L).name("All children").build();
        final InstitutionResource institution1 = InstitutionResource.builder().id(23L).name("Animals").build();
        final List<InstitutionResource> institutions = List.of(institution, institution1);
        final Category toys = Category.builder().id(11L).name("toys").build();
        final Category books = Category.builder().id(122L).name("books").build();
        final List<Category> categories = List.of(toys, books);
        when(institutionServiceMock.findAll()).thenReturn(institutions);
        when(categoryServiceMock.findAll()).thenReturn(categories);

        mockMvc.perform(get("/app/donation"))
                .andExpect(model().attribute("institutions", institutions))
                .andExpect(model().attribute("categories", categories))
                .andExpect(model().attribute("donation", new DonationResource()))
                .andExpect(status().isOk())
                .andExpect(view().name("/user/donation/form"));
        verify(institutionServiceMock).findAll();
        verify(categoryServiceMock).findAll();
    }

    @Test
    void shouldSaveDonation() throws Exception {
        mockMvc.perform(post("/app/donation/add").with(csrf())
                .flashAttr("donation", donation))
                .andExpect(status().is(302))
                .andExpect(redirectedUrl("/app/donation/confirmation"));
        verify(donationServiceMock).save(donation);
    }

    @Test
    void shouldPassDonationOnToAddAction() throws Exception {
        mockMvc.perform(post("/app/donation").with(csrf())
                .flashAttr("donation", donation))
                .andExpect(status().is(302))
                .andExpect(redirectedUrl("/app/donation/add"));
    }

    @Test
    void shouldNotPassDonationOn() throws Exception {
        final Institution institution1 = Institution.builder().id(23L).name("Animals").build();
        final DonationResource invalidDonation = DonationResource.builder()
                .institution(institution1)
                .phoneNumber("+48 404 404")
                .pickUpDate(LocalDate.now().plusMonths(1))
                .pickUpTime(LocalTime.now())
                .quantity(2)
                .zipCode("34-33JLkjl3")
                .build();

        mockMvc.perform(post("/app/donation").with(csrf())
                .flashAttr("donation", invalidDonation))
                .andExpect(status().isOk())
                .andExpect(model().errorCount(5))
                .andExpect(model().attributeHasFieldErrors("donation", "categories",
                        "city", "street", "zipCode", "phoneNumber"))
                .andExpect(view().name("/user/donation/form"));
    }

    @Test
    void shouldPrepareSummaryPage() throws Exception {
        mockMvc.perform(get("/app/donation/add")
                .sessionAttr("donation", donation))
                .andExpect(status().isOk())
                .andExpect(view().name("/user/donation/summary"))
                .andExpect(model().attribute("donation", donation));
    }

    @Test
    void shouldPrepareConfirmationPage() throws Exception {
        mockMvc.perform(get("/app/donation/confirmation")
                .flashAttr("donation", donation))
                .andExpect(status().isOk())
                .andExpect(view().name("/user/donation/form-confirmation"));
        verify(emailServiceMock).sendDonationConfirmation(donation);
    }

}