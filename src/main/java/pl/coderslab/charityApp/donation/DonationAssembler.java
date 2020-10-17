package pl.coderslab.charityApp.donation;

import org.springframework.stereotype.Component;

@Component
public class DonationAssembler {
    public Donation fromResource(DonationResource donationResource) {
        return Donation.builder()
                .id(donationResource.getId())
                .zipCode(donationResource.getZipCode())
                .quantity(donationResource.getQuantity())
                .street(donationResource.getStreet())
                .pickUpTime(donationResource.getPickUpTime())
                .pickUpDate(donationResource.getPickUpDate())
                .phoneNumber(donationResource.getPhoneNumber())
                .city(donationResource.getCity())
                .institution(donationResource.getInstitution())
                .categories(donationResource.getCategories())
                .pickUpComment(donationResource.getPickUpComment())
                .build();
    }

    public DonationResource toResource(Donation donation) {
        return DonationResource.builder()
                .id(donation.getId())
                .zipCode(donation.getZipCode())
                .quantity(donation.getQuantity())
                .street(donation.getStreet())
                .pickUpTime(donation.getPickUpTime())
                .pickUpDate(donation.getPickUpDate())
                .phoneNumber(donation.getPhoneNumber())
                .city(donation.getCity())
                .institution(donation.getInstitution())
                .categories(donation.getCategories())
                .pickUpComment(donation.getPickUpComment())
                .build();
    }

    public DonationListResource toListResource(Donation donation) {
        return DonationListResource.builder()
                .id(donation.getId())
                .actualPickUpDate(donation.getActualPickUpDate())
                .created(donation.getCreated())
                .isPickedUp(donation.getIsPickedUp())
                .institution(donation.getInstitution())
                .build();
    }

    public DonationToDisplayResource toDisplayResource(Donation donation) {
        return DonationToDisplayResource.builder()
                .id(donation.getId())
                .actualPickUpDate(donation.getActualPickUpDate())
                .created(donation.getCreated())
                .isPickedUp(donation.getIsPickedUp())
                .institution(donation.getInstitution())
                .categories(donation.getCategories())
                .pickUpDate(donation.getPickUpDate())
                .quantity(donation.getQuantity())
                .updated(donation.getUpdated())
                .build();
    }
}
