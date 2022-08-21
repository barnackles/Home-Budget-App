package com.barnackles.category;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;


    public List<Category> findAll() {
        log.info("Categories found.");
        return categoryRepository.findAll();
    }

    public List<Category> findAll(int pageNumber, int pageSize, String sortBy) {

        pageNumber -= 1;
        Pageable paging = PageRequest.of(pageNumber, pageSize, Sort.by(sortBy));
        Page<Category> pagedResult = categoryRepository.findAll(paging);

        if(pagedResult.hasContent()) {
            log.info("Categories for pageNumber: {}, pageSize: {}, sorted by: {} found", pageNumber, pageSize, sortBy);
            return pagedResult.getContent();
        } else {
            log.info("No results found.");
            return new ArrayList<>();
        }
    }

    public Category findCategoryByCategoryName(String categoryName) throws EntityNotFoundException {
        log.info("Category found: {}", categoryName);
        return categoryRepository.findCategoryByCategoryName(categoryName).orElseThrow(() -> {
                    log.error("entity with category name: {} not found", categoryName);
                    throw new EntityNotFoundException("entity not found");
                }
        );
    }
    public Category findCategoryByCategoryId(Long id) throws EntityNotFoundException {
        log.info("Category found: {}", id);
        return categoryRepository.findCategoryByCategoryId(id).orElseThrow(() -> {
                    log.error("entity with budget id: {} not found", id);
                    throw new EntityNotFoundException("entity not found");
                }
        );
    }

    public Category save(Category category) {
        return categoryRepository.save(category);
    }


    public Category update(Category category) {
        return categoryRepository.save(category);
    }

    public void delete(Category category) {
        categoryRepository.delete(category);
    }
    public Long showNumberOfCategories(){
        return categoryRepository.count();
    }





}
