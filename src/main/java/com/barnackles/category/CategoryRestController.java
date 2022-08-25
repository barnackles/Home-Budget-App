package com.barnackles.category;

import com.barnackles.category.admin.CategoryCreateDto;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/category")
public class CategoryRestController {

    private final CategoryService categoryService;

    private final ModelMapper modelMapper;



    @Secured("ROLE_USER")
    @GetMapping("/categories/all")
    public ResponseEntity<List<CategoryResponseDto>> findAll() {

        List<Category> categories = categoryService.findAll();
        List<CategoryResponseDto> listOfCategoryResponseDtos = categories
                .stream()
                .map(this::convertToCategoryResponseDto)
                .toList();
        return new ResponseEntity<>(listOfCategoryResponseDtos, HttpStatus.OK);
    }

    @Secured("ROLE_USER")
    @GetMapping("/categories/all/{pageNumber}/{pageSize}")
    public ResponseEntity<List<CategoryResponseDto>> findAll(@PathVariable int pageNumber, @PathVariable int pageSize) {
        String sortBy = "name";
        List<Category> categories = categoryService.findAll(pageNumber, pageSize, sortBy);
        List<CategoryResponseDto> listOfCategoryResponseDtos = categories
                .stream()
                .map(this::convertToCategoryResponseDto)
                .toList();
        return new ResponseEntity<>(listOfCategoryResponseDtos, HttpStatus.OK);
    }

    @Secured("ROLE_USER")
    @GetMapping("/category/{categoryName}")
    public ResponseEntity<CategoryResponseDto> findCategoryByName(@PathVariable String categoryName) {

        Category category = categoryService.findCategoryByCategoryName(categoryName);

        CategoryResponseDto categoryResponseDto = convertToCategoryResponseDto(category);

        return new ResponseEntity<>(categoryResponseDto, HttpStatus.OK);
    }



    private Category convertCreateDtoToBudget(CategoryCreateDto categoryCreateDto) {
        return modelMapper.map(categoryCreateDto, Category.class);
    }

    private CategoryResponseDto convertToCategoryResponseDto(Category category) {
        return modelMapper.map(category, CategoryResponseDto.class);
    }

}
