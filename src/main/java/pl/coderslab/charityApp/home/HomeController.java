package pl.coderslab.charityApp.home;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import pl.coderslab.charityApp.donation.DonationService;
import pl.coderslab.charityApp.institution.Institution;
import pl.coderslab.charityApp.institution.InstitutionResource;
import pl.coderslab.charityApp.institution.InstitutionService;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/")
public class HomeController {
    private final InstitutionService institutionService;
    private final DonationService donationService;

    @GetMapping
    public String home(Model model) {
        log.info("Looking for all institutions list...");
        List<InstitutionResource> institutions = institutionService.findAll();
        model.addAttribute("institutions", institutions);
        log.debug("{} institutions has been found", institutions.size());
        log.info("Counting total number of bags...");
        int totalBags = donationService.countTotalBags();
        log.debug("Total number of bags is: {}.", institutions.size());
        model.addAttribute("totalBags", totalBags);
        log.info("Counting total number of donations...");
        int totalDonations = donationService.countTotalDonations();
        log.debug("Total number of donation is: {}.", institutions.size());
        model.addAttribute("totalDonations", totalDonations);
        return "index";
    }
}
