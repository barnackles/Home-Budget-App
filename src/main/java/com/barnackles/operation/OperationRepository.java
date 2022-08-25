package com.barnackles.operation;

import com.barnackles.budget.Budget;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@Transactional
public interface OperationRepository extends JpaRepository<Operation, Long> {


    List<Operation> findAll();

    Page<Operation> findAll(Pageable pageable);

    Page<Operation> findOperationsByBudgetEquals(Pageable pageable, Budget budget);

    List<Operation> findOperationsByBudgetEquals(Budget budget);

    Optional<Operation> findOperationById(Long id);

    Optional<Operation> findOperationByUuid(UUID uuid);

    boolean existsById(Long id);

    boolean existsByUuid(UUID uuid);

    long count();

}
