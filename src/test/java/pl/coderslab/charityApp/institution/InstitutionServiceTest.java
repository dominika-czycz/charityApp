package pl.coderslab.charityApp.institution;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
class InstitutionServiceTest {
    @Autowired
    private InstitutionService testObject;
    @MockBean
    private InstitutionRepository institutionsRepositoryMock;

    @Test
    void shouldReturnAllInstitutions() {
        //given
        final Institution institution = Institution.builder().id(10L).name("All children").build();
        final Institution institution1 = Institution.builder().id(23L).name("Animals").build();
        final List<Institution> institutions = List.of(institution, institution1);
        when(institutionsRepositoryMock.findAll()).thenReturn(institutions);
        //when
        final List<Institution> actualList = testObject.findAll();
        assertThat(actualList, is(institutions));
    }
}