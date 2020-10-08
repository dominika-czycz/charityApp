package pl.coderslab.charityApp.donation;

import lombok.*;
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
    @ManyToMany
    private Set<Category> categories;
    @ManyToOne
    private Institution institution;
    @NotBlank
    @Column(nullable = false)
    private String city;
    @NotBlank
    @Pattern(regexp = "(\\d){2}-(\\d){3}")
    @Column(nullable = false)
    private String zipCode;
    @NotBlank
    @Column(nullable = false)
    @Future
    private LocalDate pickUpDate;
    @NotBlank
    @Column(nullable = false)
    private LocalTime pickUpTime;
    private String pickUpComment;
}
