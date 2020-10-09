package pl.coderslab.charityApp.institution;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.util.NumberUtils;

import java.util.Optional;

public class InstitutionConverter implements Converter<String, Institution> {
    private InstitutionRepository institutionRepository;

    @Autowired
    public void setInstitutionRepository(InstitutionRepository institutionRepository) {
        this.institutionRepository = institutionRepository;
    }

    @Override
    public Institution convert(String s) {
        final long id = NumberUtils.parseNumber(s, Long.class);
        final Optional<Institution> institution = institutionRepository.findById(id);
        if (institution.isEmpty()) {
            throw new IllegalStateException("Category does not exist");
        }
        return institution.get();
    }
}
