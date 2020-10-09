package pl.coderslab.charityApp.category;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.util.NumberUtils;

import java.util.Optional;

public class CategoryConverter implements Converter<String, Category> {
    private CategoryRepository categoryRepository;

    @Autowired
    public void setCategoryRepository(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public Category convert(String s) {
        final long id = NumberUtils.parseNumber(s, Long.class);
        final Optional<Category> category = categoryRepository.findById(id);
        if (category.isEmpty()) {
            throw new IllegalStateException("Category does not exist");
        }
        return category.get();
    }
}
