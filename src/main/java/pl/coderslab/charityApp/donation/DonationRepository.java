package pl.coderslab.charityApp.donation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DonationRepository extends JpaRepository<Donation, Long> {
    @Query("SELECT SUM (d.quantity) FROM Donation d WHERE d.pickUpDate < current_date or (d.pickUpDate = current_date and d.pickUpTime < current_time ) ")
    Optional<Integer> countTotalBags();

    @Query("SELECT SUM (d.id) FROM Donation d WHERE d.pickUpDate < current_date or (d.pickUpDate = current_date and d.pickUpTime < current_time ) ")
    Optional<Integer> countDistinctByPickUpDateBefore(LocalDate localDate);

    @Query("SELECT DISTINCT d FROM Donation d  ORDER BY d.isPickedUp, d.actualPickUpDate DESC , d.created DESC ")
    List<Donation> findAllOfUserOrderByStatusAndDates(Long principalId);

}
