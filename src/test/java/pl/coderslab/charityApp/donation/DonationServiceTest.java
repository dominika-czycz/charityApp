package pl.coderslab.charityApp.donation;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import pl.coderslab.charityApp.category.Category;
import pl.coderslab.charityApp.institution.Institution;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;
import java.util.Set;

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
    private DonationRepository donationRepositoryMock;

    @Test
    void shouldReturnTotalBagsNumber() {
        //given
        Integer expectedBagsNumber = 22;
        when(donationRepositoryMock.countTotalBags()).thenReturn(Optional.of(expectedBagsNumber));
        //when
        final int actualBagsNumber = testObject.countTotalBags();
        //then
        verify(donationRepositoryMock).countTotalBags();
        assertThat(actualBagsNumber, is(expectedBagsNumber));
    }

    @Test
    void shouldReturnZeroBags() {
        when(donationRepositoryMock.countTotalBags()).thenReturn(Optional.empty());
        final int expectedBagNum = 0;

        final int actualBagsNum = testObject.countTotalBags();

        assertThat(actualBagsNum, is(expectedBagNum));
    }

    @Test
    void shouldReturnTotalDonationNumber() {
        Integer expectedDonationNumber = 10;
        final LocalDate now = LocalDate.now();
        when(donationRepositoryMock
                .countDistinctByPickUpDateBefore(now)).thenReturn(Optional.of(expectedDonationNumber));

        final int actualDonationNumber = testObject.countTotalDonations();

        verify(donationRepositoryMock).countDistinctByPickUpDateBefore(now);
        assertThat(actualDonationNumber, is(expectedDonationNumber));
    }

    @Test
    void shouldReturnZeroDonations() {
        final LocalDate now = LocalDate.now();
        when(donationRepositoryMock.countDistinctByPickUpDateBefore(now)).thenReturn(Optional.empty());
        final int expectedDonationNum = 0;

        final int actualDonationsNum = testObject.countTotalDonations();

        verify(donationRepositoryMock).countDistinctByPickUpDateBefore(now);
        assertThat(actualDonationsNum, is(expectedDonationNum));
    }

    @Test
    void shouldSaveDonation() {
        final Institution institution1 = Institution.builder().id(23L).name("Animals").build();
        final Category toys = Category.builder().id(11L).name("toys").build();
        final Category books = Category.builder().id(122L).name("books").build();
        final Set<Category> categories = Set.of(toys, books);
        final Donation donationToSave = Donation.builder()
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
        final Donation savedDonation = donationToSave.toBuilder().id(111L).build();
        when(donationRepositoryMock.save(donationToSave)).thenReturn(savedDonation);
        testObject.save(donationToSave);
        verify(donationRepositoryMock).save(donationToSave);
    }
}