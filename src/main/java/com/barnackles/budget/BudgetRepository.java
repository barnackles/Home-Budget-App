package com.barnackles.budget;

import com.barnackles.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public interface BudgetRepository extends JpaRepository<Budget, Long> {

    Optional<Budget> findBudgetByBudgetName(String budgetName);

    Optional<Budget> findBudgetById(Long id);

    Optional<Budget> findBudgetByBudgetNameAndUserEquals(String budgetName, User user);

    List<Budget> findAll();

    Page<Budget> findAll(Pageable pageable);

    @Override
    boolean existsById(Long id);
}
