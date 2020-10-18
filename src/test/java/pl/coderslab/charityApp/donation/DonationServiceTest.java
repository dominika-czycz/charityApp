package pl.coderslab.charityApp.donation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import pl.coderslab.charityApp.category.Category;
import pl.coderslab.charityApp.exceptions.NotExistingRecordException;
import pl.coderslab.charityApp.institution.Institution;
import pl.coderslab.charityApp.user.OrdinaryUserResource;
import pl.coderslab.charityApp.user.User;
import pl.coderslab.charityApp.user.UserService;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
class DonationServiceTest {
    @Autowired
    private DonationService testObject;
    @Autowired
    private DonationAssembler donationAssembler;
    @MockBean
    private DonationRepository donationRepositoryMock;
    @MockBean
    private UserService userServiceMock;

    private Institution institution;
    private Set<Category> categories;
    private User user;

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
        user = User.builder()
                .id(1112L)
                .firstName("Jim")
                .lastName("Generous")
                .password("Password2020?")
                .email("test@email")
                .build();

        when(userServiceMock.getPrincipalResource()).thenReturn(userResource);
        when(userServiceMock.getPrincipal()).thenReturn(user);
    }

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
    @WithMockUser(username = "user@test")
    void shouldSaveDonation() throws NotExistingRecordException {
        final Donation donationToSave = Donation.builder()
                .categories(categories)
                .institution(institution)
                .city("Wrocław")
                .phoneNumber("+48 404 404 404")
                .pickUpDate(LocalDate.now().plusMonths(1))
                .pickUpTime(LocalTime.now())
                .street("Wrocławska")
                .quantity(2)
                .zipCode("34-333")
                .build();
        final DonationResource donationResource = donationAssembler.toResource(donationToSave);
        final Donation savedDonation = donationToSave.toBuilder().id(111L).build();
        when(donationRepositoryMock.save(donationToSave)).thenReturn(savedDonation);

        testObject.save(donationResource);

        verify(donationRepositoryMock).save(donationToSave);
    }

    @Test
    @WithMockUser(username = "user@test")
    void shouldReturnAllPrincipalDonationsList() throws NotExistingRecordException {
        final Donation donation = Donation.builder()
                .id(11L)
                .isPickedUp(true)
                .created(LocalDate.now().minusDays(10))
                .actualPickUpDate(LocalDate.now().minusDays(3))
                .institution(institution)
                .build();
        final Donation donation2 = Donation.builder()
                .id(1333L)
                .isPickedUp(false)
                .created(LocalDate.now().minusDays(2))
                .institution(institution)
                .build();
        final List<Donation> donations = List.of(donation, donation2);
        final List<DonationListResource> expected = donations
                .stream()
                .map(donationAssembler::toListResource)
                .collect(Collectors.toList());
        when(donationRepositoryMock.findAllOfUserOrderByStatusAndDates(user.getId()))
                .thenReturn(donations);

        final List<DonationListResource> actual = testObject.findAllOfPrincipalSortedByStatusAndDates();

        assertThat(actual, is(expected));
    }

    @Test
    @WithMockUser(username = "user@test")
    void shouldReturnDonationResourceToDisplay() throws NotExistingRecordException {
        final Donation toDisplay = spy(Donation.class);
        toDisplay.setId(11L);
        toDisplay.setIsPickedUp(true);
        toDisplay.setCreated(LocalDate.now().minusDays(10));
        toDisplay.setActualPickUpDate(LocalDate.now().minusDays(3));
        toDisplay.setInstitution(institution);
        when(donationRepositoryMock.findById(toDisplay.getId()))
                .thenReturn(Optional.of(toDisplay));
        final DonationToDisplayResource expected = donationAssembler.toDisplayResource(toDisplay);


        final DonationToDisplayResource actual =
                testObject.getResourceToDisplayById(toDisplay.getId());

        verify(toDisplay, atLeast(2)).getCategories();
        assertThat(actual, is(expected));
    }

    @Test
    @WithMockUser(username = "user@test")
    void shouldChangeStatusIfActualDateIsNotNull() throws NotExistingRecordException {
        Long id = 11L;
        final Donation toEdit = Donation.builder()
                .id(id)
                .isPickedUp(false)
                .created(LocalDate.now().minusDays(10))
                .institution(institution)
                .build();
        final DonationToUpdateResource resourceWithNewData = DonationToUpdateResource.builder()
                .id(id)
                .actualPickUpDate(LocalDate.now())
                .isPickedUp(true)
                .build();
        final Donation edited = toEdit.toBuilder()
                .actualPickUpDate(resourceWithNewData.getActualPickUpDate())
                .isPickedUp(resourceWithNewData.getIsPickedUp()).build();
        when(donationRepositoryMock.findById(toEdit.getId()))
                .thenReturn(Optional.of(toEdit));

        testObject.changeStatus(resourceWithNewData);

        verify(donationRepositoryMock).save(edited);
    }

    @Test
    @WithMockUser(username = "user@test")
    void shouldChangeStatusEvenIfActualDateIsNull() throws NotExistingRecordException {
        Long id = 11L;
        final Donation toEdit = Donation.builder()
                .id(id)
                .isPickedUp(false)
                .pickUpDate(LocalDate.now().minusDays(2))
                .created(LocalDate.now().minusDays(10))
                .institution(institution)
                .build();
        final DonationToUpdateResource resourceWithNewData = DonationToUpdateResource.builder()
                .id(id)
                .isPickedUp(true)
                .build();
        final Donation edited = toEdit.toBuilder()
                .actualPickUpDate(toEdit.getPickUpDate())
                .isPickedUp(resourceWithNewData.getIsPickedUp()).build();
        when(donationRepositoryMock.findById(toEdit.getId()))
                .thenReturn(Optional.of(toEdit));

        testObject.changeStatus(resourceWithNewData);

        verify(donationRepositoryMock).save(edited);
    }


}