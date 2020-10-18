package pl.coderslab.charityApp.donation.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import pl.coderslab.charityApp.donation.DonationService;
import pl.coderslab.charityApp.donation.resources.DonationListResource;
import pl.coderslab.charityApp.donation.resources.DonationToDisplayResource;
import pl.coderslab.charityApp.donation.resources.DonationToUpdateResource;
import pl.coderslab.charityApp.exceptions.NotExistingRecordException;
import pl.coderslab.charityApp.user.UserService;

import java.util.List;

@Controller
@RequestMapping("/app/donations")
@RequiredArgsConstructor
@SessionAttributes({"userName"})
@Slf4j
public class DonationsController {
    private final DonationService donationService;
    private final UserService userService;

    @GetMapping
    public String prepareListPage(Model model) throws NotExistingRecordException {
        log.info("Looking for all institutions list...");
        List<DonationListResource> donations = donationService.findAllOfPrincipalSortedByStatusAndDates();
        model.addAttribute("donations", donations);
        log.debug("{} donations have been found", donations.size());
        return "/user/donations/list";
    }

    @GetMapping("/edit")
    public String prepareEditPage(@RequestParam Long id, Model model) throws NotExistingRecordException {
        log.debug("Preparing edit page for entity with id {}.", id);
        DonationToDisplayResource donation = donationService.getResourceToDisplayById(id);
        model.addAttribute("donationToDisplay", donation);
        model.addAttribute("donationToUpdate", new DonationToUpdateResource());
        return "/user/donations/edit";
    }

    @PostMapping("/edit")
    public String processEditPage(@ModelAttribute("donationToUpdate") DonationToUpdateResource donation) throws NotExistingRecordException {
        donationService.changeStatus(donation);
        return "redirect:/app/donations";
    }

    @ModelAttribute("userName")
    public String userResource(Model model) throws NotExistingRecordException {
        final Object userName = model.getAttribute("userName");
        return (userName == null) ? userService.getPrincipalResource().getFirstName() : (String) userName;
    }

}
