package pl.coderslab.charityApp.institution;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.coderslab.charityApp.exceptions.NotExistingRecordException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class InstitutionsServiceImpl implements InstitutionService {
    private final InstitutionRepository institutionsRepository;
    private final InstitutionAssembler institutionAssembler;

    @Override
    public List<InstitutionResource> findAll() {
        return institutionsRepository
                .findAll()
                .stream()
                .map(institutionAssembler::toResource)
                .collect(Collectors.toList());
    }

    @Override
    public void save(InstitutionResource institutionResource) {
        log.debug("Saving entity from resource {}...", institutionResource);
        final Institution institution = institutionAssembler.fromResource(institutionResource);
        final Institution saved = institutionsRepository.save(institution);
        log.debug("Entity {} has been saved.", saved);
    }

    @Override
    public InstitutionResource getResourceById(Long id) throws NotExistingRecordException {
        return institutionsRepository.findById(id)
                .map(institutionAssembler::toResource)
                .orElseThrow(new NotExistingRecordException("Institution with id " + id + " does not exist!"));
    }

    @Override
    public void edit(InstitutionResource institutionResource) throws NotExistingRecordException {
        log.debug("Resource {} with new data", institutionResource);
        final Institution toEdit = getInstitution(institutionResource.getId());
        log.debug("Updating entity: {}....", toEdit);
        toEdit.setName(institutionResource.getName());
        toEdit.setDescription(institutionResource.getDescription());
        final Institution saved = institutionsRepository.save(toEdit);
        log.debug("Entity {} has been updated.", saved);
    }

    private Institution getInstitution(Long id) throws NotExistingRecordException {
        return institutionsRepository.findById(id)
                .orElseThrow(new NotExistingRecordException("Institution with id " + id
                        + " does not exist!"));
    }

    @Override
    public void delete(Long id) throws NotExistingRecordException {
        log.debug("Preparing to delete entity with id {}...", id);
        final Institution toDelete = getInstitution(id);
        log.debug("Deleting entity: {}....", toDelete);
        institutionsRepository.delete(toDelete);
        log.debug("Entity {} has been deleted.", toDelete);
    }
}
