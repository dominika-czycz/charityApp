package pl.coderslab.charityApp.donation;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

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
    void shouldReturnZero() {
        //given
        when(donationRepository.countTotalBags()).thenReturn(Optional.empty());
        final int expectedBagNum = 0;
        //when
        final int actualBagsNum = testObject.countTotalBags();
        //then
        assertThat(actualBagsNum, is(expectedBagNum));
    }
}