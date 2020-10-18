package pl.coderslab.charityApp.email;

import pl.coderslab.charityApp.donation.DonationResource;
import pl.coderslab.charityApp.exceptions.NotExistingRecordException;
import pl.coderslab.charityApp.user.OrdinaryUserResource;

import javax.mail.MessagingException;

public interface EmailService {
    void sendRegistrationConfirmation(OrdinaryUserResource user) throws MessagingException, NotExistingRecordException;

    void sendDonationConfirmation(DonationResource donation) throws MessagingException, NotExistingRecordException;
}
