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
import pl.coderslab.charityApp.donation.controllers.DonationsController;
import pl.coderslab.charityApp.donation.resources.DonationListResource;
import pl.coderslab.charityApp.donation.resources.DonationToDisplayResource;
import pl.coderslab.charityApp.donation.resources.DonationToUpdateResource;
import pl.coderslab.charityApp.exceptions.NotExistingRecordException;
import pl.coderslab.charityApp.institution.Institution;
import pl.coderslab.charityApp.user.resources.OrdinaryUserResource;
import pl.coderslab.charityApp.user.UserService;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DonationsController.class)
@ActiveProfiles("test")
@WithMockUser
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class DonationsControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private DonationService donationServiceMock;
    @MockBean
    private UserService userServiceMock;
    private Institution institution;
    private Set<Category> categories;

    @BeforeEach
    void setUp() throws NotExistingRecordException {
        institution = Institution.builder().id(23L).name("Animals").build();
        final Category toys = Category.builder().id(11L).name("toys").build();
        final Category books = Category.builder().id(122L).name("books").build();
        categories = Set.of(toys, books);

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
    void shouldPrepareListPage() throws Exception {
        final DonationListResource donation = DonationListResource.builder()
                .isPickedUp(true)
                .created(LocalDate.now().minusDays(10))
                .actualPickUpDate(LocalDate.now().minusDays(3))
                .institution(institution)
                .build();
        final DonationListResource donation2 = DonationListResource.builder()
                .isPickedUp(false)
                .created(LocalDate.now().minusDays(2))
                .institution(institution)
                .build();
        final List<DonationListResource> donations = List.of(donation, donation2);
        when(donationServiceMock.findAllOfPrincipalSortedByStatusAndDates())
                .thenReturn(donations);

        mockMvc.perform(get("/app/donations"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("donations", donations))
                .andExpect(view().name("/user/donations/list"));
        verify(donationServiceMock).findAllOfPrincipalSortedByStatusAndDates();
    }

    @Test
    void shouldPrepareEditPage() throws Exception {
        final Long id = 2222L;
        final DonationToDisplayResource toDisplay = DonationToDisplayResource.builder()
                .id(id)
                .isPickedUp(true)
                .created(LocalDate.now().minusDays(10))
                .actualPickUpDate(LocalDate.now().minusDays(3))
                .institution(institution)
                .quantity(4)
                .categories(categories)
                .pickUpDate(LocalDate.now().plusDays(2))
                .build();
        when(donationServiceMock.getResourceToDisplayById(id)).thenReturn(toDisplay);
        final DonationToUpdateResource toUpdate = new DonationToUpdateResource();

        mockMvc.perform(get("/app/donations/edit")
                .param("id", id.toString()))
                .andExpect(status().is2xxSuccessful())
                .andExpect(model().attribute("donationToDisplay", toDisplay))
                .andExpect(model().attribute("donationToUpdate", toUpdate))
                .andExpect(view().name("/user/donations/edit"));
        verify(donationServiceMock).getResourceToDisplayById(id);
    }

    @Test
    void shouldUpdateEntityFromValidResource() throws Exception {
        final Long id = 2222L;
        final DonationToUpdateResource resourceWithNewData = DonationToUpdateResource.builder()
                .id(id)
                .actualPickUpDate(LocalDate.now())
                .isPickedUp(true)
                .build();

        mockMvc.perform(post("/app/donations/edit").with(csrf())
                .flashAttr("donationToUpdate", resourceWithNewData))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/app/donations"));
        verify(donationServiceMock).changeStatus(resourceWithNewData);
    }

    @Test
    void shouldUpdateWhenActualDateIsNull() throws Exception {
        final Long id = 2222L;
        final DonationToUpdateResource resourceWithNewData = DonationToUpdateResource.builder()
                .id(id)
                .actualPickUpDate(null)
                .isPickedUp(true)
                .build();

        mockMvc.perform(post("/app/donations/edit").with(csrf())
                .flashAttr("donationToUpdate", resourceWithNewData))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/app/donations"));
        verify(donationServiceMock).changeStatus(resourceWithNewData);
    }


}