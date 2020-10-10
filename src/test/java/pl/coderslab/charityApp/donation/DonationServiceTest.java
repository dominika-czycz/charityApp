package pl.coderslab.charityApp.donation;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
class DonationServiceTest {
    @Autowired
    private DonationService testObject;
    @MockBean
    private DonationRepository donationRepository;

    @Test
    void shouldReturnTotalBagsNumber() {
        //given
        Integer expectedBagsNumber = 22;
        when(donationRepository.countTotalBags()).thenReturn(Optional.of(expectedBagsNumber));
        //when
        final int actualBagsNumber = testObject.countTotalBags();
        //then
        verify(donationRepository).countTotalBags();
        assertThat(actualBagsNumber, is(expectedBagsNumber));
    }

    @Test
    void shouldReturnZeroBags() {
        when(donationRepository.countTotalBags()).thenReturn(Optional.empty());
        final int expectedBagNum = 0;

        final int actualBagsNum = testObject.countTotalBags();

        assertThat(actualBagsNum, is(expectedBagNum));
    }

    @Test
    void shouldReturnTotalDonationNumber() {
        Integer expectedDonationNumber = 10;
        final LocalDate now = LocalDate.now();
        when(donationRepository
                .countDistinctByPickUpDateBefore(now)).thenReturn(Optional.of(expectedDonationNumber));

        final int actualDonationNumber = testObject.countTotalDonations();

        verify(donationRepository).countDistinctByPickUpDateBefore(now);
        assertThat(actualDonationNumber, is(expectedDonationNumber));
    }

    @Test
    void shouldReturnZeroDonations() {
        final LocalDate now = LocalDate.now();
        when(donationRepository.countDistinctByPickUpDateBefore(now)).thenReturn(Optional.empty());
        final int expectedDonationNum = 0;

        final int actualDonationsNum = testObject.countTotalDonations();

        verify(donationRepository).countDistinctByPickUpDateBefore(now);
        assertThat(actualDonationsNum, is(expectedDonationNum));
    }
}