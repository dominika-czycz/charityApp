package pl.coderslab.charityApp.home;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import pl.coderslab.charityApp.institution.Institution;
import pl.coderslab.charityApp.institution.InstitutionService;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/")
public class HomeController {
    private final InstitutionService institutionService;
    @GetMapping
    public String home(Model model) {
        log.info("Looking for all institutions list...");
        List<Institution> institutions = institutionService.findAll();
        model.addAttribute("institutions", institutionService.findAll());
        log.debug("{} institutions has been found", institutions.size());
//        log.debug("\n ADDING NUMBER OF ALL BAGS TO MODEL");
//        model.addAttribute("bagsNumber", donationService.countBug());
//        log.debug("\n ADDING NUMBER OF ALL PICK UP BAGS TO MODEL");
//        model.addAttribute("allGifts", donationService.numberOfAllDonations());
        return "index";
    }
}
