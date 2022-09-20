package com.barnackles.category.admin;

import com.barnackles.category.Category;
import com.barnackles.category.CategoryService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityExistsException;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/category")
public class CategoryAdminRestController {

    private final CategoryService categoryService;
    private final ModelMapper modelMapper;


    @Secured("ROLE_ADMIN")
    @GetMapping("/categories/all")
    public ResponseEntity<List<CategoryAdminResponseDto>> findAll() {

        List<Category> categories = categoryService.findAll();
        List<CategoryAdminResponseDto> listOfCategoryAdminResponseDtos = categories
                .stream()
                .map(this::convertToCategoryAdminResponseDto)
                .toList();
        return new ResponseEntity<>(listOfCategoryAdminResponseDtos, HttpStatus.OK);
    }

    @Secured("ROLE_ADMIN")
    @GetMapping("/categories/all/{pageNumber}/{pageSize}/{sortBy}")
    public ResponseEntity<List<CategoryAdminResponseDto>> findAll(@PathVariable int pageNumber, @PathVariable int pageSize,
                                                                  @PathVariable String sortBy) {

        List<Category> categories = categoryService.findAll(pageNumber, pageSize, sortBy);
        List<CategoryAdminResponseDto> listOfCategoryAdminResponseDtos = categories
                .stream()
                .map(this::convertToCategoryAdminResponseDto)
                .toList();
        return new ResponseEntity<>(listOfCategoryAdminResponseDtos, HttpStatus.OK);
    }


    @Secured("ROLE_ADMIN")
    @GetMapping("/category/id/{id}")
    public ResponseEntity<CategoryAdminResponseDto> findCategoryById(@PathVariable Long id) {

        Category category = categoryService.findCategoryByCategoryId(id);

        CategoryAdminResponseDto categoryAdminResponseDto = convertToCategoryAdminResponseDto(category);

        return new ResponseEntity<>(categoryAdminResponseDto, HttpStatus.OK);
    }

    @Secured("ROLE_ADMIN")
    @PostMapping("/category")
    public ResponseEntity<CategoryAdminResponseDto> createCategory(@Valid @RequestBody CategoryCreateDto categoryCreateDto) {
        Category category = convertCreateDtoToCategory(categoryCreateDto);


        if (!categoryService.existsByName(category.getName().toLowerCase())) {
            category.setName(category.getName().toLowerCase());
            categoryService.save(category);

            CategoryAdminResponseDto categoryAdminResponseDto = convertToCategoryAdminResponseDto(category);
            return new ResponseEntity<>(categoryAdminResponseDto, HttpStatus.CREATED);

        }

        throw new EntityExistsException("Category with this name already exists.");
    }

    @Secured("ROLE_ADMIN")
    @PutMapping("/category")
    public ResponseEntity<CategoryAdminResponseDto> updateCategory(@Valid @RequestBody CategoryAdminUpdateDto categoryAdminUpdateDto) {

        Category category = categoryService.findCategoryByCategoryId(categoryAdminUpdateDto.getId());
        category.setName(categoryAdminUpdateDto.getName());
        categoryService.update(category);

        CategoryAdminResponseDto categoryAdminResponseDto = convertToCategoryAdminResponseDto(category);
        return new ResponseEntity<>(categoryAdminResponseDto, HttpStatus.OK);

    }

    @Secured("ROLE_ADMIN")
    @DeleteMapping("/category/{categoryId}")
    public ResponseEntity<String> deleteCategory(@PathVariable Long categoryId) {

        String message = String.format("Category of id: %d successfully deleted ", categoryId);
        categoryService.delete(categoryService.findCategoryByCategoryId(categoryId));
        return new ResponseEntity<>(message, HttpStatus.OK);
    }

    @Secured("ROLE_ADMIN")
    @GetMapping("/category/count")
    public ResponseEntity<String> showNumberOfCategories() {

        Long numberOfCategories = categoryService.showNumberOfCategories();
        String message = String.format("There are %d categories.", numberOfCategories);

        return new ResponseEntity<>(message, HttpStatus.OK);
    }


    private Category convertCreateDtoToCategory(CategoryCreateDto categoryCreateDto) {
        return modelMapper.map(categoryCreateDto, Category.class);
    }

    private CategoryAdminResponseDto convertToCategoryAdminResponseDto(Category category) {
        return modelMapper.map(category, CategoryAdminResponseDto.class);
    }

}
