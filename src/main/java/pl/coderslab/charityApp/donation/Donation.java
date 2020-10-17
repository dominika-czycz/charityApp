package pl.coderslab.charityApp.donation;

import lombok.*;
import pl.coderslab.charityApp.category.Category;
import pl.coderslab.charityApp.institution.Institution;
import pl.coderslab.charityApp.user.User;

import javax.persistence.*;
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
@ToString(exclude = "categories")
public class Donation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer quantity;

    @ManyToMany
    private Set<Category> categories;

    @ManyToOne
    private User user;

    @ManyToOne
    private Institution institution;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private String street;

    @Column(nullable = false)
    private String zipCode;

    @Column(nullable = false, name = "phone_number")
    private String phoneNumber;

    @Column(nullable = false, name = "pick_up_date")
    private LocalDate pickUpDate;

    @Column(nullable = false, name = "pick_up_time")
    private LocalTime pickUpTime;

    @Column(name = "pick_up_comment")
    private String pickUpComment;

    @Column(name = "actual_pick_up_date")
    private LocalDate actualPickUpDate;

    @Column(nullable = false, name = "is_picked_up")
    private Boolean isPickedUp;

    @Column(nullable = false)
    private LocalDate created;

    private LocalDate updated;


    @PrePersist
    private void prePersist() {
        isPickedUp = false;
        created = LocalDate.now();
    }

    @PreUpdate
    private void preUpdate() {
        created = LocalDate.now();
    }

}
