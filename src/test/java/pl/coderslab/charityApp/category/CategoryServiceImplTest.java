package pl.coderslab.charityApp.category;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.*;


class CategoryServiceImplTest {
    private CategoryService testObject;
    private CategoryRepository categoryRepositoryMock;

    @BeforeEach
    void setUp() {
        categoryRepositoryMock = mock(CategoryRepository.class);
        testObject = new CategoryServiceImpl(categoryRepositoryMock);
    }

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