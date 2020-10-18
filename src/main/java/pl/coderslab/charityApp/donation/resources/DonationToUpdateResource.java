package pl.coderslab.charityApp.donation.resources;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class DonationToUpdateResource {
    private Long id;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate actualPickUpDate;

    private Boolean isPickedUp;

}
