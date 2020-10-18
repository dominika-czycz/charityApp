package pl.coderslab.charityApp.donation.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import pl.coderslab.charityApp.category.Category;
import pl.coderslab.charityApp.category.CategoryService;
import pl.coderslab.charityApp.donation.resources.DonationResource;
import pl.coderslab.charityApp.donation.DonationService;
import pl.coderslab.charityApp.email.EmailService;
import pl.coderslab.charityApp.exceptions.NotExistingRecordException;
import pl.coderslab.charityApp.institution.InstitutionResource;
import pl.coderslab.charityApp.institution.InstitutionService;
import pl.coderslab.charityApp.user.UserService;

import javax.mail.MessagingException;
import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/app/donation")
@RequiredArgsConstructor
@SessionAttributes({"donation", "userName"})
@Slf4j
public class DonationFormController {

    private final InstitutionService institutionService;
    private final CategoryService categoryService;
    private final DonationService donationService;
    private final EmailService emailService;
    private final UserService userService;


    @GetMapping
    public String prepareForm(Model model) {
        model.addAttribute("donation", new DonationResource());
        return "/user/donation/form";
    }

    @PostMapping
    public String processForm(@ModelAttribute("donation") @Valid DonationResource donation, BindingResult bindingResult) {
        if (validation(donation, bindingResult)) return "/user/donation/form";
        log.info("Entity passes validation. Redirect to summary page...");
        return "redirect:/app/donation/add";
    }

    private boolean validation(DonationResource donation, BindingResult bindingResult) {
        log.debug("Validation of entity: {}...", donation);
        if (bindingResult.hasErrors()) {
            log.warn("Entity {} fails validation!", donation);
            return true;
        }
        return false;
    }

    @GetMapping("/add")
    public String prepareSummaryPage() {
        log.info("Preparing summary page...");
        return "/user/donation/summary";
    }

    @PostMapping("/add")
    public String processSummaryPage(@ModelAttribute("donation") @Valid DonationResource donation, BindingResult bindingResult) throws NotExistingRecordException {
        if (validation(donation, bindingResult)) return "/user/form";
        log.debug("Preparing to save entity: {} ...", donation);
        donationService.save(donation);
        return "redirect:/app/donation/confirmation";
    }

    @GetMapping("/confirmation")
    public String prepareConfirmationPage(@ModelAttribute("donation") DonationResource donation, WebRequest request) throws NotExistingRecordException, MessagingException {
        log.info("Preparing confirmation page...");
        emailService.sendDonationConfirmation(donation);
        request.removeAttribute("donation", WebRequest.SCOPE_SESSION);
        return "/user/donation/form-confirmation";
    }

    @ModelAttribute("userName")
    public String userResource(Model model) throws NotExistingRecordException {
        final Object userName = model.getAttribute("userName");
        return (userName == null) ? userService.getPrincipalResource().getFirstName() : (String) userName;
    }

    @ModelAttribute("institutions")
    private List<InstitutionResource> institutions() {
        return institutionService.findAll();
    }

    @ModelAttribute("categories")
    private List<Category> categories() {
        return categoryService.findAll();
    }
}

