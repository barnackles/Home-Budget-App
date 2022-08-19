package com.barnackles.budget;

import com.barnackles.user.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.util.List;

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



    public List<Budget> findAll() {
        return budgetRepository.findAll();
    }

    public Budget save(Budget budget) {
        return budgetRepository.save(budget);
    }

    public void delete(Budget budget) {
        budgetRepository.delete(budget);
    }
}
