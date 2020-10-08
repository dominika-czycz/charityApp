package pl.coderslab.charityApp.donation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@Slf4j
@RequiredArgsConstructor
public class DonationServiceImpl implements DonationService {
    private final DonationRepository donationRepository;

    @Override
    public int countTotalBags() {
        return donationRepository.countTotalBags().orElse(0);
    }

    @Override
    public int countTotalDonations() {
        return donationRepository.countDistinctByPickUpDateBefore(LocalDate.now())
                .orElse(0);
    }
}
