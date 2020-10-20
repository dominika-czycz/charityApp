package pl.coderslab.charityApp.institution;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.coderslab.charityApp.exceptions.NotExistingRecordException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class InstitutionServiceImplTest {
    private InstitutionService testObject;
    private InstitutionAssembler institutionAssembler;
    private InstitutionRepository institutionsRepositoryMock;

    @BeforeEach
    void setUp() {
        institutionAssembler = new InstitutionAssembler();
        institutionsRepositoryMock = mock(InstitutionRepository.class);
        testObject = new InstitutionsServiceImpl(institutionsRepositoryMock, institutionAssembler);
    }

    @Test
    void shouldReturnAllInstitutions() {
        final Institution institution = Institution.builder().id(10L).name("All children").build();
        final Institution institution1 = Institution.builder().id(23L).name("Animals").build();
        final List<Institution> institutions = List.of(institution, institution1);
        when(institutionsRepositoryMock.findAll()).thenReturn(institutions);
        final List<InstitutionResource> expected = institutions.stream()
                .map(institutionAssembler::toResource)
                .collect(Collectors.toList());

        final List<InstitutionResource> actualList = testObject.findAll();

        assertThat(actualList, is(expected));
    }

    @Test
    void shouldSaveEntityFromResource() {
        final InstitutionResource resourceToSave = InstitutionResource.builder()
                .name("All children")
                .description("We help all children")
                .build();
        final Institution toSave = institutionAssembler.fromResource(resourceToSave);
        final Institution saved = toSave.toBuilder().id(2233L).build();
        when(institutionsRepositoryMock.save(toSave)).thenReturn(saved);

        testObject.save(resourceToSave);
        verify(institutionsRepositoryMock).save(toSave);
    }

    @Test
    void shouldEditEntityFromResource() throws NotExistingRecordException {
        final Long id = 2233L;
        final InstitutionResource resourceToEdit = InstitutionResource.builder()
                .id(id)
                .name("All children")
                .description("We help all children")
                .build();
        final Institution toEditFromDb = Institution.builder()
                .id(id)
                .name("Animals")
                .description("We help all animals")
                .build();
        final Institution toEdit = institutionAssembler.fromResource(resourceToEdit);
        when(institutionsRepositoryMock.save(toEdit)).thenReturn(toEdit);
        when(institutionsRepositoryMock.findById(id)).thenReturn(Optional.of(toEditFromDb));

        testObject.edit(resourceToEdit);

        verify(institutionsRepositoryMock).save(toEdit);
    }

    @Test
    void shouldThrowNotExistingRecordException() {
        final Long id = 2233L;
        final InstitutionResource resourceToEdit = InstitutionResource.builder()
                .id(id)
                .name("Not existing foundation")
                .description("We help nobody")
                .build();
        when(institutionsRepositoryMock.findById(id)).thenReturn(Optional.empty());
        assertThrows(NotExistingRecordException.class,
                () -> testObject.edit(resourceToEdit));
    }

    @Test
    void shouldDeleteEntityById() throws NotExistingRecordException {
        final Long id = 2233L;
        final Institution toDelete = Institution.builder()
                .id(id)
                .name("Animals")
                .description("We help all animals")
                .build();
        when(institutionsRepositoryMock.findById(id)).thenReturn(Optional.of(toDelete));

        testObject.delete(id);

        verify(institutionsRepositoryMock).delete(toDelete);
    }

    @Test
    void shouldFindResourceById() throws NotExistingRecordException {
        final Long id = 2233L;
        final Institution toFind = Institution.builder()
                .id(id)
                .name("Animals")
                .description("We help all animals")
                .build();
        final InstitutionResource expected = institutionAssembler.toResource(toFind);
        when(institutionsRepositoryMock.findById(id)).thenReturn(Optional.of(toFind));

        final InstitutionResource resourceById = testObject.getResourceById(id);

        verify(institutionsRepositoryMock).findById(id);
        assertThat(resourceById, is(expected));
    }
}