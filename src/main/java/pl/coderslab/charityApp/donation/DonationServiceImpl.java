package pl.coderslab.charityApp.donation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.coderslab.charityApp.donation.resources.DonationListResource;
import pl.coderslab.charityApp.donation.resources.DonationResource;
import pl.coderslab.charityApp.donation.resources.DonationToDisplayResource;
import pl.coderslab.charityApp.donation.resources.DonationToUpdateResource;
import pl.coderslab.charityApp.exceptions.NotExistingRecordException;
import pl.coderslab.charityApp.user.User;
import pl.coderslab.charityApp.user.UserService;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class DonationServiceImpl implements DonationService {
    private final DonationRepository donationRepository;
    private final DonationAssembler donationAssembler;
    private final UserService userService;

    @Override
    public int countTotalBags() {
        return donationRepository.countTotalBags().orElse(0);
    }

    @Override
    public int countTotalDonations() {
        return donationRepository.countDistinctByPickUpDateBefore(LocalDate.now())
                .orElse(0);
    }

    @Override
    @Transactional
    public void save(DonationResource donation) throws NotExistingRecordException {
        final User principal = userService.getPrincipal();
        final Donation toSave = donationAssembler.fromResource(donation);
        toSave.setUser(principal);
        final Donation saved = donationRepository.save(toSave);
        log.debug("Entity {} has been saved", saved);
    }

    @Override
    public List<DonationListResource> findAllOfPrincipalSortedByStatusAndDates() throws NotExistingRecordException {
        final Long principalId = userService.getPrincipal().getId();
        return donationRepository.findAllOfUserOrderByStatusAndDates(principalId)
                .stream()
                .map(donationAssembler::toListResource)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public DonationToDisplayResource getResourceToDisplayById(Long id) throws NotExistingRecordException {
        final Donation toDisplay = getDonation(id);
        Hibernate.initialize(toDisplay.getCategories());
        return donationAssembler.toDisplayResource(toDisplay);
    }

    private Donation getDonation(Long id) throws NotExistingRecordException {
        return donationRepository.findById(id)
                .orElseThrow(new NotExistingRecordException("Donation with id " + id + " does not exist!"));
    }

    @Override
    public void changeStatus(DonationToUpdateResource donation) throws NotExistingRecordException {
        final Donation toEdit = getDonation(donation.getId());
        log.debug("Updating entity:  {}... ", toEdit);
        toEdit.setIsPickedUp(donation.getIsPickedUp());
        final LocalDate actualPickUpDate = donation.getActualPickUpDate();
        if (actualPickUpDate == null) {
            toEdit.setActualPickUpDate(toEdit.getPickUpDate());
        } else {
            toEdit.setActualPickUpDate(actualPickUpDate);
        }
        final Donation updated = donationRepository.save(toEdit);
        log.debug("Entity {} has been updated.", updated);
    }
}
