package pl.coderslab.charityApp.email;

import pl.coderslab.charityApp.donation.resources.DonationResource;
import pl.coderslab.charityApp.exceptions.NotExistingRecordException;
import pl.coderslab.charityApp.user.resources.OrdinaryUserResource;
import pl.coderslab.charityApp.user.resources.ToChangePasswordUserResource;

import javax.mail.MessagingException;

public interface EmailService {
    void sendRegistrationConfirmation(OrdinaryUserResource user) throws MessagingException, NotExistingRecordException;

    void sendDonationConfirmation(DonationResource donation) throws MessagingException, NotExistingRecordException;

    void sendPasswordResetLink(ToChangePasswordUserResource userResource) throws MessagingException;
}
