package com.barnackles.operation.admin;

import com.barnackles.budget.Budget;
import com.barnackles.budget.BudgetService;
import com.barnackles.operation.Operation;
import com.barnackles.operation.OperationCreateDto;
import com.barnackles.operation.OperationService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/operation")
public class OperationAdminRestController {

    private final OperationService operationService;
    private final BudgetService budgetService;
    private final ModelMapper modelMapper;

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/operations/all")
    public ResponseEntity<List<OperationAdminResponseDto>> findAll() {

        List<Operation> operations = operationService.findAll();
        List<OperationAdminResponseDto> listOfOperationAdminResponseDtos = operations
                .stream()
                .map(this::convertToOperationAdminResponseDto)
                .toList();

        return new ResponseEntity<>(listOfOperationAdminResponseDtos, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/operations/all/{pageNumber}/{pageSize}/{sortBy}")
    public ResponseEntity<List<OperationAdminResponseDto>> findAll(@PathVariable int pageNumber, @PathVariable int pageSize,
                                                                @PathVariable String sortBy) {

        List<Operation> operations = operationService.findAll(pageNumber, pageSize, sortBy);
        List<OperationAdminResponseDto> listOfOperationAdminResponseDtos = operations
                .stream()
                .map(this::convertToOperationAdminResponseDto)
                .toList();
        return new ResponseEntity<>(listOfOperationAdminResponseDtos, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping ("/operation/{operationId}")
    public ResponseEntity<OperationAdminResponseDto> findOperationById(@PathVariable Long operationId) {

        Operation operation = operationService.findOperationByOperationId(operationId);
        OperationAdminResponseDto operationAdminResponseDto = convertToOperationAdminResponseDto(operation);

        return new ResponseEntity<>(operationAdminResponseDto, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/operation/{budgetId}")
    public ResponseEntity<OperationAdminResponseDto> createOperationForBudget(@Valid @RequestBody OperationCreateDto operationCreateDto,
                                                                      @PathVariable Long budgetId) {
        Budget budget = budgetService.findBudgetByBudgetId(budgetId);
        Operation operation = convertCreateDtoToOperation(operationCreateDto);
        operationService.save(operation);

        List<Operation> operationList = budget.getOperations();
        operationList.add(operation);
        budget.setOperations(operationList);
        budgetService.update(budget);

        OperationAdminResponseDto operationAdminResponseDto = convertToOperationAdminResponseDto(operation);
        operationAdminResponseDto.setBudgetId(budget.getId());
        operationAdminResponseDto.setBudgetName(budget.getBudgetName());
        return new ResponseEntity<>(operationAdminResponseDto, HttpStatus.CREATED);

    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/operation/{operationId}")
    public ResponseEntity<OperationAdminResponseDto> updateOperation(@Valid @RequestBody OperationCreateDto operationCreateDto,
                                                 @PathVariable Long operationId) {

        Operation persistentOperation = operationService.findOperationByOperationId(operationId);
        Operation operation = convertCreateDtoToOperation(operationCreateDto);
        operation.setId(operationId);
        operation.setUuid(persistentOperation.getUuid());

        operationService.update(operation);

        OperationAdminResponseDto operationAdminResponseDto = convertToOperationAdminResponseDto(operation);
        return new ResponseEntity<>(operationAdminResponseDto, HttpStatus.OK);

    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/operation/{operationId}")
    public ResponseEntity<String> deleteOperation(@PathVariable Long operationId) {

        String message = String.format("Operation of id: %d successfully deleted ", operationId);
        operationService.delete(operationService.findOperationByOperationId(operationId));
        return new ResponseEntity<>(message, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/operation/count")
    public ResponseEntity<String> showNumberOfOperations() {

        Long numberOfOperations = operationService.showNumberOfOperations();
        String message = String.format("There are %d operations.", numberOfOperations);

        return new ResponseEntity<>(message, HttpStatus.OK);
    }



    private Operation convertCreateDtoToOperation(OperationCreateDto operationCreateDto) {
        return modelMapper.map(operationCreateDto, Operation.class);
    }

    private OperationAdminResponseDto convertToOperationAdminResponseDto(Operation operation) {
        return modelMapper.map(operation, OperationAdminResponseDto.class);
    }



}
