package pl.coderslab.charityApp.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import pl.coderslab.charityApp.donation.DonationService;
import pl.coderslab.charityApp.exceptions.NotExistingRecordException;
import pl.coderslab.charityApp.institution.InstitutionResource;
import pl.coderslab.charityApp.institution.InstitutionService;
import pl.coderslab.charityApp.user.UserService;

import java.util.List;

@Controller
@Slf4j
@RequestMapping("/app/admin")
@RequiredArgsConstructor
@SessionAttributes("userName")
public class AdminHomeController {
    private final InstitutionService institutionService;
    private final DonationService donationService;
    private final UserService userService;

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
        return "/admin/home";
    }

    @ModelAttribute("userName")
    public String userResource(Model model) throws NotExistingRecordException {
        final Object userName = model.getAttribute("userName");
        return (userName == null) ? userService.getPrincipalResource().getFirstName() : (String) userName;
    }
}
