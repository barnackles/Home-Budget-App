package com.barnackles.category;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.Optional;

@Repository
@Transactional
public interface CategoryRepository extends JpaRepository<Category, Long> {


    @Override
    Page<Category> findAll(Pageable pageable);

    @Override
    <S extends Category> S save(S entity);

    @Override
    Optional<Category> findById(Long id);

    @Override
    boolean existsById(Long id);

    @Override
    long count();

    @Override
    void deleteById(Long id);
}
