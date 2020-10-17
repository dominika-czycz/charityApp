package pl.coderslab.charityApp.donation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.coderslab.charityApp.institution.Institution;

import java.time.LocalDate;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class DonationListResource {
    private Long id;
    private Boolean isPickedUp;
    private LocalDate actualPickUpDate;
    private LocalDate created;
    private Institution institution;
}
