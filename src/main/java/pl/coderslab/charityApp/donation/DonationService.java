package pl.coderslab.charityApp.donation;

import pl.coderslab.charityApp.donation.resources.DonationListResource;
import pl.coderslab.charityApp.donation.resources.DonationResource;
import pl.coderslab.charityApp.donation.resources.DonationToDisplayResource;
import pl.coderslab.charityApp.donation.resources.DonationToUpdateResource;
import pl.coderslab.charityApp.exceptions.NotExistingRecordException;

import java.util.List;

public interface DonationService {
    int countTotalBags();

    int countTotalDonations();

    void save(DonationResource donation) throws NotExistingRecordException;

    List<DonationListResource> findAllOfPrincipalSortedByStatusAndDates() throws NotExistingRecordException;

    DonationToDisplayResource getResourceToDisplayById(Long id) throws NotExistingRecordException;

    void changeStatus(DonationToUpdateResource donation) throws NotExistingRecordException;
}
