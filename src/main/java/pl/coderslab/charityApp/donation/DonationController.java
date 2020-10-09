package pl.coderslab.charityApp.donation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import pl.coderslab.charityApp.category.Category;
import pl.coderslab.charityApp.category.CategoryService;
import pl.coderslab.charityApp.institution.Institution;
import pl.coderslab.charityApp.institution.InstitutionService;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/donation")
@RequiredArgsConstructor
@Slf4j
public class DonationController {

    private final InstitutionService institutionService;
    private final CategoryService categoryService;
    private final DonationService donationService;


    @GetMapping
    public String prepareForm(Model model) {
        model.addAttribute("donation", new Donation());
        return "/user/form";
    }

    @PostMapping
    public String processForm(@Valid Donation donation, BindingResult bindingResult) {
        log.debug("Preparing to save the entity: {}...", donation);
        if (bindingResult.hasErrors()) {
            log.warn("Entity {} fails validation!", donation);
            return "/user/form";
        }
        log.info("Saving...");
        donationService.save(donation);
        return "redirect:/";
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

