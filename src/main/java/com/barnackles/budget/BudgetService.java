package com.barnackles.budget;

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
import java.util.ArrayList;
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


    public boolean checkIfUserHasBudgetWithGivenName (String budgetName, User user) {
        boolean userHasBudget = budgetRepository.findBudgetByBudgetNameAndUserEquals(budgetName, user).isPresent();
        log.info("User has budget with {}: {}", budgetName, userHasBudget);
        return userHasBudget;
    }



    public List<Budget> findAll() {
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
}
