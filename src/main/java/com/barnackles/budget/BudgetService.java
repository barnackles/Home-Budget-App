package com.barnackles.budget;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class BudgetService {

    private final BudgetRepository budgetRepository;

    Budget save(Budget budget) {
        return budgetRepository.save(budget);
    }
}
