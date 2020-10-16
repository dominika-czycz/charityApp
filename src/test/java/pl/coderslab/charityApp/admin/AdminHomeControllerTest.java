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
import pl.coderslab.charityApp.donation.DonationService;
import pl.coderslab.charityApp.exceptions.NotExistingRecordException;
import pl.coderslab.charityApp.institution.InstitutionResource;
import pl.coderslab.charityApp.institution.InstitutionService;
import pl.coderslab.charityApp.user.OrdinaryUserResource;
import pl.coderslab.charityApp.user.UserService;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminHomeController.class)
@WithMockUser(roles = {"ADMIN", "SUPER_ADMIN"}, username = "admin@test")
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class AdminHomeControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private DonationService donationService;
    @MockBean
    private InstitutionService institutionService;
    @MockBean
    private UserService userServiceMock;

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
    void shouldReturnAdminHomePageWithInstitutionListAndTotalBagsAndTotalDonationsNumbers() throws Exception {
        //given
        final InstitutionResource institution = InstitutionResource.builder().id(10L).name("All children").build();
        final InstitutionResource institution1 = InstitutionResource.builder().id(23L).name("Animals").build();
        final List<InstitutionResource> institutions = List.of(institution, institution1);
        int totalBags = 12;
        int totalDonations = 10;
        when(institutionService.findAll()).thenReturn(institutions);
        when(donationService.countTotalBags()).thenReturn(totalBags);
        when(donationService.countTotalDonations()).thenReturn(totalDonations);
        //when, then
        mockMvc.perform(get("/app/admin"))
                .andExpect(model().attribute("institutions", institutions))
                .andExpect(model().attribute("totalBags", totalBags))
                .andExpect(model().attribute("totalDonations", totalDonations))
                .andExpect(status().isOk())
                .andExpect(view().name("/admin/home"));
        verify(institutionService).findAll();
        verify(donationService).countTotalBags();
        verify(donationService).countTotalDonations();
    }

}