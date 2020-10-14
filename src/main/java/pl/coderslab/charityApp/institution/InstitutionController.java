package pl.coderslab.charityApp.institution;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import pl.coderslab.charityApp.exceptions.NotExistingRecordException;
import pl.coderslab.charityApp.user.UserResource;
import pl.coderslab.charityApp.user.UserService;

import javax.validation.Valid;
import java.util.List;

@Controller
@Slf4j
@RequestMapping("/app/admin/institution")
@RequiredArgsConstructor
@SessionAttributes("userResource")
public class InstitutionController {
    private final InstitutionService institutionService;
    private final UserService userService;

    @GetMapping
    public String prepareInstitutionList(Model model) {
        log.info("Looking for all institutions list...");
        List<Institution> institutions = institutionService.findAll();
        model.addAttribute("institutions", institutions);
        log.debug("{} institutions has been found", institutions.size());
        return "/admin/institution/list";
    }

    @GetMapping("/add")
    public String prepareAddPage(Model model) {
        log.info("Preparing edit page ...");
        model.addAttribute(new InstitutionResource());
        return "/admin/institution/add";
    }

    @PostMapping("/add")
    public String processAddPage(@Valid InstitutionResource institutionResource, BindingResult result) {
        if (result.hasErrors()) {
            log.warn("Resource {} fails validation", institutionResource);
            return "/admin/institution/add";
        }
        institutionService.save(institutionResource);
        return "redirect:/app/admin/institution";
    }

    @GetMapping("/edit")
    public String prepareEditPage(@RequestParam Long id, Model model) throws NotExistingRecordException {
        log.debug("Preparing edit page for entity with id {}.", id);
        InstitutionResource institutionResource = institutionService.getResourceById(id);
        model.addAttribute(institutionResource);
        return "/admin/institution/edit";
    }

    @PostMapping("/edit")
    public String processEditPage(@Valid InstitutionResource institutionResource, BindingResult result) throws NotExistingRecordException {
        if (result.hasErrors()) {
            log.warn("Resource {} fails validation", institutionResource);
            return "/admin/institution/edit";
        }
        institutionService.edit(institutionResource);
        return "redirect:/app/admin/institution";
    }

    @GetMapping("/delete")
    public String prepareDeletePage(@RequestParam Long id, Model model) throws NotExistingRecordException {
        log.debug("Preparing delete page for entity with id {}.", id);
        InstitutionResource institutionResource = institutionService.getResourceById(id);
        model.addAttribute(institutionResource);
        return "/admin/institution/delete";
    }

    @PostMapping("/delete")
    public String processDeletePage(@RequestParam Long id) throws NotExistingRecordException {
        institutionService.delete(id);
        return "redirect:/app/admin/institution";
    }

    @ModelAttribute("userResource")
    public UserResource userResource(Model model) throws NotExistingRecordException {
        final Object userResource = model.getAttribute("userResource");
        return (userResource == null) ? userService.getPrincipalResource() : (UserResource) userResource;
    }
}
