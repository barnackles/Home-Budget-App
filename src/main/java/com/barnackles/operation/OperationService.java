package com.barnackles.operation;

import com.barnackles.budget.Budget;
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
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class OperationService {

    private final OperationRepository operationRepository;

    public List<Operation> findAll() {
        log.info("Operations found.");
        return operationRepository.findAll();
    }

    public List<Operation> findAll(int pageNumber, int pageSize, String sortBy) {

        pageNumber -= 1;
        Pageable paging = PageRequest.of(pageNumber, pageSize, Sort.by(sortBy));
        Page<Operation> pagedResult = operationRepository.findAll(paging);

        if (pagedResult.hasContent()) {
            log.info("Operations for pageNumber: {}, pageSize: {}, sorted by: {} found", pageNumber, pageSize, sortBy);
            return pagedResult.getContent();
        } else {
            log.info("No results found.");
            return new ArrayList<>();
        }
    }

    public List<Operation> findAllOperationsForBudget(int pageNumber, int pageSize, String sortBy,
                                                      Budget budget) {

        pageNumber -= 1;
        Pageable paging = PageRequest.of(pageNumber, pageSize, Sort.by(sortBy));
        Page<Operation> pagedResult = operationRepository.findOperationsByBudgetEquals(paging, budget);

        if (pagedResult.hasContent()) {
            log.info("Operations for pageNumber: {}, pageSize: {}, sorted by: {} found", pageNumber, pageSize, sortBy);
            return pagedResult.getContent();
        } else {
            log.info("No results found.");
            return new ArrayList<>();
        }
    }


    public Operation findOperationByOperationId(Long id) throws EntityNotFoundException {
        log.info("Operation found: {}", id);
        return operationRepository.findOperationById(id).orElseThrow(() -> {
                    log.error("entity with operation id: {} not found", id);
                    throw new EntityNotFoundException("entity not found");
                }
        );
    }


    public Operation findOperationByOperationUuid(UUID uuid) throws EntityNotFoundException {
        log.info("Operation found: {}", uuid);
        return operationRepository.findOperationByUuid(uuid).orElseThrow(() -> {
                    log.error("entity with operation id: {} not found", uuid);
                    throw new EntityNotFoundException("entity not found");
                }
        );
    }


    public Operation save(Operation operation) {
        return operationRepository.save(operation);
    }


    public Operation update(Operation operation) {
        return operationRepository.save(operation);
    }

    public void delete(Operation operation) {
        operationRepository.delete(operation);
    }

    public Long showNumberOfOperations() {
        return operationRepository.count();
    }


}
