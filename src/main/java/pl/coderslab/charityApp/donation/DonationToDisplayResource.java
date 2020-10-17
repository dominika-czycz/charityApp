package pl.coderslab.charityApp.donation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.coderslab.charityApp.category.Category;
import pl.coderslab.charityApp.institution.Institution;

import java.time.LocalDate;
import java.util.Set;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class DonationToDisplayResource {
    private Long id;

    private Integer quantity;

    private Set<Category> categories;

    private Institution institution;

    private LocalDate pickUpDate;

    private LocalDate actualPickUpDate;

    private Boolean isPickedUp;

    private LocalDate created;

    private LocalDate updated;
}
