package pl.coderslab.charityApp.donation;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import pl.coderslab.charityApp.category.Category;
import pl.coderslab.charityApp.category.CategoryService;
import pl.coderslab.charityApp.institution.Institution;
import pl.coderslab.charityApp.institution.InstitutionService;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DonationController.class)
@ActiveProfiles("test")
@WithMockUser
class DonationControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private DonationService donationService;
    @MockBean
    private CategoryService categoryService;
    @MockBean
    private InstitutionService institutionService;

    @Test
    void shouldDisplayDonationForm() throws Exception {
        //given
        final Institution institution = Institution.builder().id(10L).name("All children").build();
        final Institution institution1 = Institution.builder().id(23L).name("Animals").build();
        final List<Institution> institutions = List.of(institution, institution1);
        final Category toys = Category.builder().id(11L).name("toys").build();
        final Category books = Category.builder().id(122L).name("books").build();
        final List<Category> categories = List.of(toys, books);
        when(institutionService.findAll()).thenReturn(institutions);
        when(categoryService.findAll()).thenReturn(categories);
        //when, then
        mockMvc.perform(get("/donation"))
                .andExpect(model().attribute("institutions", institutions))
                .andExpect(model().attribute("categories", categories))
                .andExpect(model().attribute("donation", new Donation()))
                .andExpect(status().isOk())
                .andExpect(view().name("/user/form"));
        verify(institutionService).findAll();
        verify(categoryService).findAll();
    }

    @Test
    void shouldSaveDonation() throws Exception {
        //given
        final Institution institution1 = Institution.builder().id(23L).name("Animals").build();
        final Category toys = Category.builder().id(11L).name("toys").build();
        final Category books = Category.builder().id(122L).name("books").build();
        final Set<Category> categories = Set.of(toys, books);
        final Donation donation = Donation.builder()
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
        //when, then
        mockMvc.perform(post("/donation").with(csrf())
                .flashAttr("donation", donation))
                .andExpect(status().is(302))
                .andExpect(redirectedUrl("/"));
        verify(donationService).save(donation);
    }

    @Test
    void shouldNotSaveDonation() throws Exception {
        //given
        final Institution institution1 = Institution.builder().id(23L).name("Animals").build();
        final Donation invalidDonation = Donation.builder()
                .institution(institution1)
                .phoneNumber("+48 404 404")
                .pickUpDate(LocalDate.now().plusMonths(1))
                .pickUpTime(LocalTime.now())
                .quantity(2)
                .zipCode("34-33JLkjl3")
                .build();
        //when, then
        mockMvc.perform(post("/donation").with(csrf())
                .flashAttr("donation", invalidDonation))
                .andExpect(status().isOk())
                .andExpect(model().errorCount(5))
                .andExpect(model().attributeHasFieldErrors("donation", "categories",
                        "city", "street", "zipCode", "phoneNumber"))
                .andExpect(view().name("/user/form"));
        verify(donationService, atMost(0)).save(invalidDonation);
    }

}