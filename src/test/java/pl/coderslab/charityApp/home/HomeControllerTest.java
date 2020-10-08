package pl.coderslab.charityApp.home;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import pl.coderslab.charityApp.donation.DonationService;
import pl.coderslab.charityApp.institution.Institution;
import pl.coderslab.charityApp.institution.InstitutionService;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(HomeController.class)
@WithAnonymousUser
@ActiveProfiles("test")
class HomeControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private DonationService donationService;
    @MockBean
    private InstitutionService institutionService;

    @Test
    void shouldReturnIndexViewWithInstitutionListAndTotalBagsAndTotalDonationsNumbers() throws Exception {
        //given
        final Institution institution = Institution.builder().id(10L).name("All children").build();
        final Institution institution1 = Institution.builder().id(23L).name("Animals").build();
        final List<Institution> institutions = List.of(institution, institution1);
        int totalBags = 12;
        int totalDonations = 10;
        when(institutionService.findAll()).thenReturn(institutions);
        when(donationService.countTotalBags()).thenReturn(totalBags);
        when(donationService.countTotalDonations()).thenReturn(totalDonations);
        //when, then
        mockMvc.perform(get("/"))
                .andExpect(model().attribute("institutions", institutions))
                .andExpect(model().attribute("totalBags", totalBags))
                .andExpect(model().attribute("totalDonations", totalDonations))
                .andExpect(status().isOk())
                .andExpect(view().name("index"));
        verify(institutionService).findAll();
        verify(donationService).countTotalBags();
        verify(donationService).countTotalDonations();
    }
}