package pl.coderslab.charityApp.institution;

import pl.coderslab.charityApp.exceptions.NotExistingRecordException;

import java.util.List;

public interface InstitutionService {
    List<Institution> findAll();

    void save(InstitutionResource institutionResource);

    InstitutionResource getResourceById(Long id) throws NotExistingRecordException;

    void edit(InstitutionResource institutionResource) throws NotExistingRecordException;

    void delete(Long id) throws NotExistingRecordException;
}
