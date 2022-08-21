package com.barnackles.category;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public interface CategoryRepository extends JpaRepository<Category, Long> {


    List<Category> findAll();
    @Override
    Page<Category> findAll(Pageable pageable);

    Optional<Category> findCategoryById(Long id);

    Optional<Category> findCategoryByName(String Name);

    boolean existsByName(String name);

    @Override
    long count();

}
