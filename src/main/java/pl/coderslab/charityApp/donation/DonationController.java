package pl.coderslab.charityApp.donation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import pl.coderslab.charityApp.category.Category;
import pl.coderslab.charityApp.category.CategoryService;
import pl.coderslab.charityApp.email.EmailService;
import pl.coderslab.charityApp.exceptions.NotExistingRecordException;
import pl.coderslab.charityApp.institution.Institution;
import pl.coderslab.charityApp.institution.InstitutionService;

import javax.mail.MessagingException;
import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/app/donation")
@RequiredArgsConstructor
@SessionAttributes("donation")
@Slf4j
public class DonationController {

    private final InstitutionService institutionService;
    private final CategoryService categoryService;
    private final DonationService donationService;
    private final EmailService emailService;


    @GetMapping
    public String prepareForm(Model model) {
        model.addAttribute("donation", new Donation());
        return "/user/form";
    }

    @PostMapping
    public String processForm(@Valid Donation donation, BindingResult bindingResult) {
        if (validation(donation, bindingResult)) return "/user/form";
        log.info("Entity passes validation. Redirect to summary page...");
        return "redirect:/app/donation/add";
    }

    private boolean validation(@Valid Donation donation, BindingResult bindingResult) {
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
        return "/user/summary";
    }

    @PostMapping("/add")
    public String processSummaryPage(@Valid Donation donation, BindingResult bindingResult) {
        if (validation(donation, bindingResult)) return "/user/form";
        log.debug("Preparing to save entity: {} ...", donation);
        donationService.save(donation);
        return "redirect:/app/donation/confirmation";
    }

    @GetMapping("/confirmation")
    public String prepareConfirmationPage(Donation donation, WebRequest request, Model model) throws NotExistingRecordException, MessagingException {
        log.info("Preparing confirmation page...");
        emailService.sendDonationConfirmation(donation);
        request.removeAttribute("donation", WebRequest.SCOPE_SESSION);
        return "/user/form-confirmation";
    }

    @ModelAttribute("institutions")
    private List<Institution> institutions() {
        return institutionService.findAll();
    }

    @ModelAttribute("categories")
    private List<Category> categories() {
        return categoryService.findAll();
    }
}

