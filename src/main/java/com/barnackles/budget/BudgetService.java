package com.barnackles.budget;

import com.barnackles.operation.Operation;
import com.barnackles.user.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static com.barnackles.operation.OperationType.*;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class BudgetService {

    private final BudgetRepository budgetRepository;


    public Budget findBudgetByBudgetName(String budgetName) throws EntityNotFoundException {
        log.info("Budget found: {}", budgetName);
        return budgetRepository.findBudgetByBudgetName(budgetName).orElseThrow(() -> {
                    log.error("entity with budget name: {} not found", budgetName);
                    throw new EntityNotFoundException("entity not found");
                }
        );
    }
    public Budget findBudgetByBudgetId(Long id) throws EntityNotFoundException {
        log.info("Budget found: {}", id);
        return budgetRepository.findBudgetById(id).orElseThrow(() -> {
                    log.error("entity with budget id: {} not found", id);
                    throw new EntityNotFoundException("entity not found");
                }
        );
    }

    public Budget findBudgetByBudgetNameAndUserEquals(String budgetName, User user) throws EntityNotFoundException {
        log.info("Budget found: {}", budgetName);
        return budgetRepository.findBudgetByBudgetNameAndUserEquals(budgetName, user).orElseThrow(() -> {
                    log.error("entity with budget name: {} not found", budgetName);
                    throw new EntityNotFoundException("entity not found");
                }
        );
    }


    public boolean checkIfUserHasBudgetWithGivenName (String budgetName, User user) {
        boolean userHasBudget = budgetRepository.findBudgetByBudgetNameAndUserEquals(budgetName, user).isPresent();
        log.info("User has budget with {}: {}", budgetName, userHasBudget);
        return userHasBudget;
    }



    public List<Budget> findAll() {
        log.info("Budgets found.");
        return budgetRepository.findAll();
    }

    public List<Budget> findAll(int pageNumber, int pageSize, String sortBy) {

        pageNumber -= 1;
        Pageable paging = PageRequest.of(pageNumber, pageSize, Sort.by(sortBy));
        Page<Budget> pagedResult = budgetRepository.findAll(paging);

        if(pagedResult.hasContent()) {
            log.info("Budgets for pageNumber: {}, pageSize: {}, sorted by: {} found", pageNumber, pageSize, sortBy);
            return pagedResult.getContent();
        } else {
            log.info("No results found.");
            return new ArrayList<>();
        }

    }

    public Budget save(Budget budget) {
        return budgetRepository.save(budget);
    }

    public Budget update(Budget budget) {
        return budgetRepository.save(budget);
    }

    public void delete(Budget budget) {
        budgetRepository.delete(budget);
    }

    public boolean checkIfBudgetExistsById(Long id) {
        return budgetRepository.existsById(id);
    }

    //financial methods

    public BigDecimal calculateBudgetBalance(List<Operation> operations) {

        return (calculateTotalIncome(operations).subtract(calculateTotalExpense(operations)));

    }

    public BigDecimal calculateTotalIncome(List<Operation> operations) {

        return operations.stream()
                .filter(operation -> INCOME.equals(operation.getOperationType()))
                .map(Operation::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal calculateTotalExpense(List<Operation> operations) {

        return operations.stream()
                .filter(operation -> EXPENSE.equals(operation.getOperationType()))
                .map(Operation::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

    }

    public BigDecimal calculateTotalSavings(List<Operation> operations) {

        return operations.stream()
                .filter(operation -> SAVING.equals(operation.getOperationType()))
                .map(Operation::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }





}
