package pl.coderslab.charityApp.category;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
class CategoryServiceTest {
    @Autowired
    private CategoryService testObject;
    @MockBean
    private CategoryRepository categoryRepositoryMock;

    @Test
    void shouldReturnAllCategories() {
        final Category toys = Category.builder().id(11L).name("toys").build();
        final Category books = Category.builder().id(122L).name("books").build();
        final List<Category> expectedCategories = List.of(toys, books);
        when(categoryRepositoryMock.findAll()).thenReturn(expectedCategories);

        final List<Category> actual = testObject.findAll();

        verify(categoryRepositoryMock).findAll();
        assertThat(actual, is(expectedCategories));
    }
}