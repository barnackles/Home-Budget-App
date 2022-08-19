package com.barnackles.budget;

import com.barnackles.user.User;
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

    Optional<Budget> findBudgetByBudgetNameAndUsersEquals(String budgetName, User user);
    List<Budget> findAll();

}
