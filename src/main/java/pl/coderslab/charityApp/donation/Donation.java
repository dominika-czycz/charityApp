package pl.coderslab.charityApp.donation;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import pl.coderslab.charityApp.category.Category;
import pl.coderslab.charityApp.institution.Institution;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;

@Entity
@Table(name = "donations")
@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
@Builder(toBuilder = true)
@EqualsAndHashCode(of = "id")
@ToString
public class Donation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    @Min(1)
    @Column(nullable = false)
    private Integer quantity;
    @NotEmpty
    @ManyToMany
    private Set<Category> categories;
    @ManyToOne
    @NotNull
    private Institution institution;
    @NotBlank
    @Column(nullable = false)
    private String city;
    @NotBlank
    @Column(nullable = false)
    private String street;
    @NotBlank
    @Pattern(regexp = "(\\d){2}-(\\d){3}")
    @Column(nullable = false)
    private String zipCode;
    @NotBlank
    @Pattern(regexp = "^(\\+\\d{1,3}( )?)?((\\(\\d{3}\\))|\\d{3})[- .]?\\d{3}[- .]?\\d{4}$"
            + "|^(\\+\\d{1,3}( )?)?(\\d{3}[ ]?){2}\\d{3}$"
            + "|^(\\+\\d{1,3}( )?)?(\\d{3}[ ]?)(\\d{2}[ ]?){2}\\d{2}$")
    @Column(nullable = false, name = "phone_number")
    private String phoneNumber;
    @NotNull
    @Column(nullable = false)
    @Future
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate pickUpDate;
    @NotNull
    @Column(nullable = false)
    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime pickUpTime;
    private String pickUpComment;
}
