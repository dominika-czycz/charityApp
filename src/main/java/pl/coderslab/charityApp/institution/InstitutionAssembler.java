package pl.coderslab.charityApp.institution;

import org.springframework.stereotype.Component;

@Component
public class InstitutionAssembler {
    public InstitutionResource toResource(Institution institution) {
        return InstitutionResource.builder()
                .id(institution.getId())
                .name(institution.getName())
                .description(institution.getDescription()).build();
    }

    public Institution fromResource(InstitutionResource institutionResource) {
        return Institution.builder()
                .id(institutionResource.getId())
                .name(institutionResource.getName())
                .description(institutionResource.getDescription()).build();
    }
}
