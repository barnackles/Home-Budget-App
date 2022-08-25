package com.barnackles.operation.admin;

import com.barnackles.budget.Budget;
import com.barnackles.budget.BudgetService;
import com.barnackles.category.CategoryService;
import com.barnackles.operation.*;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/operation")
public class OperationAdminRestController {

    private final OperationService operationService;
    private final BudgetService budgetService;
    private final ModelMapper modelMapper;

    private final CategoryService categoryService;

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


    //generate operations data for user

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/operations/generate")
    public ResponseEntity<String> generate() {

        Budget budget = budgetService.findBudgetByBudgetId(13L);
        List<OperationCreateDto> operationCreateDtos = List.of(

//june

        new OperationCreateDto("salary", categoryService.findCategoryByCategoryId(1L), new BigDecimal("5320.87"),
                OperationType.INCOME, LocalDateTime.of(2022, Month.JUNE, 10, 20, 40, 0,
                0), ActualOrPlanned.ACTUAL, OperationFrequency.MONTHLY),

        new OperationCreateDto("heating", categoryService.findCategoryByCategoryId(3L), new BigDecimal("120.87"),
                OperationType.EXPENSE, LocalDateTime.of(2022, Month.JUNE, 14, 20, 40, 0,
                0), ActualOrPlanned.ACTUAL, OperationFrequency.NONE),

        new OperationCreateDto("electricity", categoryService.findCategoryByCategoryId(3L), new BigDecimal("470.35"),
                OperationType.EXPENSE, LocalDateTime.of(2022, Month.JUNE, 14, 21, 40, 0,
                0), ActualOrPlanned.ACTUAL, OperationFrequency.MONTHLY),

        new OperationCreateDto("water", categoryService.findCategoryByCategoryId(3L), new BigDecimal("257.34"),
                OperationType.EXPENSE, LocalDateTime.of(2022, Month.JUNE, 14, 21, 45, 0,
                0), ActualOrPlanned.ACTUAL, OperationFrequency.MONTHLY),

        new OperationCreateDto("internet", categoryService.findCategoryByCategoryId(3L), new BigDecimal("99.99"),
                OperationType.EXPENSE, LocalDateTime.of(2022, Month.JUNE, 19, 10, 55, 0,
                0), ActualOrPlanned.ACTUAL, OperationFrequency.MONTHLY),

        new OperationCreateDto("mobile", categoryService.findCategoryByCategoryId(3L), new BigDecimal("59.99"),
                OperationType.EXPENSE, LocalDateTime.of(2022, Month.JUNE, 19, 10, 55, 0,
                0), ActualOrPlanned.ACTUAL, OperationFrequency.MONTHLY),

        new OperationCreateDto("flat rent", categoryService.findCategoryByCategoryId(3L), new BigDecimal("2100.00"),
                OperationType.EXPENSE, LocalDateTime.of(2022, Month.JUNE, 19, 10, 55, 0,
                0), ActualOrPlanned.ACTUAL, OperationFrequency.MONTHLY),

        new OperationCreateDto("food supplies", categoryService.findCategoryByCategoryId(2L), new BigDecimal("210.54"),
                OperationType.EXPENSE, LocalDateTime.of(2022, Month.JUNE,4 , 18, 55, 0,
                0), ActualOrPlanned.ACTUAL, OperationFrequency.NONE),

        new OperationCreateDto("food supplies", categoryService.findCategoryByCategoryId(2L), new BigDecimal("210.54"),
                OperationType.EXPENSE, LocalDateTime.of(2022, Month.JUNE,14 , 18, 55, 0,
                0), ActualOrPlanned.ACTUAL, OperationFrequency.NONE),

        new OperationCreateDto("food supplies", categoryService.findCategoryByCategoryId(2L), new BigDecimal("390.30"),
                OperationType.EXPENSE, LocalDateTime.of(2022, Month.JUNE,27 , 18, 55, 0,
                0), ActualOrPlanned.ACTUAL, OperationFrequency.NONE),

        new OperationCreateDto("eating out", categoryService.findCategoryByCategoryId(5L), new BigDecimal("81.34"),
                OperationType.EXPENSE, LocalDateTime.of(2022, Month.JUNE,12 , 20, 55, 0,
                0), ActualOrPlanned.ACTUAL, OperationFrequency.NONE),

        new OperationCreateDto("vod streaming", categoryService.findCategoryByCategoryId(5L), new BigDecimal("40.00"),
                OperationType.EXPENSE, LocalDateTime.of(2022, Month.JUNE,21 , 8, 25, 0,
                0), ActualOrPlanned.ACTUAL, OperationFrequency.NONE),

        new OperationCreateDto("music streaming", categoryService.findCategoryByCategoryId(5L), new BigDecimal("30.00"),
                OperationType.EXPENSE, LocalDateTime.of(2022, Month.JUNE,21 , 8, 15, 0,
                0), ActualOrPlanned.ACTUAL, OperationFrequency.NONE),

        new OperationCreateDto("fuel", categoryService.findCategoryByCategoryId(4L), new BigDecimal("347.39"),
                OperationType.EXPENSE, LocalDateTime.of(2022, Month.JUNE,21 , 8, 15, 0,
                0), ActualOrPlanned.ACTUAL, OperationFrequency.NONE),


        new OperationCreateDto("savings", categoryService.findCategoryByCategoryId(7L), new BigDecimal("200.00"),
                OperationType.SAVING, LocalDateTime.of(2022, Month.JUNE,11 , 8, 15, 0,
                0), ActualOrPlanned.ACTUAL, OperationFrequency.NONE),

//july


        new OperationCreateDto("salary", categoryService.findCategoryByCategoryId(1L), new BigDecimal("5320.87"),
                OperationType.INCOME, LocalDateTime.of(2022, Month.JULY, 10, 20, 40, 0,
                0), ActualOrPlanned.ACTUAL, OperationFrequency.MONTHLY),

        new OperationCreateDto("heating", categoryService.findCategoryByCategoryId(3L), new BigDecimal("100.87"),
                OperationType.EXPENSE, LocalDateTime.of(2022, Month.JULY, 14, 20, 40, 0,
                0), ActualOrPlanned.ACTUAL, OperationFrequency.NONE),

        new OperationCreateDto("electricity", categoryService.findCategoryByCategoryId(3L), new BigDecimal("470.35"),
                OperationType.EXPENSE, LocalDateTime.of(2022, Month.JULY, 14, 21, 40, 0,
                0), ActualOrPlanned.ACTUAL, OperationFrequency.MONTHLY),

        new OperationCreateDto("water", categoryService.findCategoryByCategoryId(3L), new BigDecimal("257.34"),
                OperationType.EXPENSE, LocalDateTime.of(2022, Month.JULY, 14, 21, 45, 0,
                0), ActualOrPlanned.ACTUAL, OperationFrequency.MONTHLY),

        new OperationCreateDto("internet", categoryService.findCategoryByCategoryId(3L), new BigDecimal("99.99"),
                OperationType.EXPENSE, LocalDateTime.of(2022, Month.JULY, 19, 10, 55, 0,
                0), ActualOrPlanned.ACTUAL, OperationFrequency.MONTHLY),

        new OperationCreateDto("mobile", categoryService.findCategoryByCategoryId(3L), new BigDecimal("59.99"),
                OperationType.EXPENSE, LocalDateTime.of(2022, Month.JULY, 19, 10, 55, 0,
                0), ActualOrPlanned.ACTUAL, OperationFrequency.MONTHLY),

        new OperationCreateDto("flat rent", categoryService.findCategoryByCategoryId(3L), new BigDecimal("2100.00"),
                OperationType.EXPENSE, LocalDateTime.of(2022, Month.JULY, 19, 10, 55, 0,
                0), ActualOrPlanned.ACTUAL, OperationFrequency.MONTHLY),

        new OperationCreateDto("food supplies", categoryService.findCategoryByCategoryId(2L), new BigDecimal("210.54"),
                OperationType.EXPENSE, LocalDateTime.of(2022, Month.JULY,4 , 18, 55, 0,
                0), ActualOrPlanned.ACTUAL, OperationFrequency.NONE),

        new OperationCreateDto("food supplies", categoryService.findCategoryByCategoryId(2L), new BigDecimal("210.54"),
                OperationType.EXPENSE, LocalDateTime.of(2022, Month.JULY,14 , 18, 55, 0,
                0), ActualOrPlanned.ACTUAL, OperationFrequency.NONE),

        new OperationCreateDto("food supplies", categoryService.findCategoryByCategoryId(2L), new BigDecimal("390.30"),
                OperationType.EXPENSE, LocalDateTime.of(2022, Month.JULY,27 , 18, 55, 0,
                0), ActualOrPlanned.ACTUAL, OperationFrequency.NONE),

        new OperationCreateDto("eating out", categoryService.findCategoryByCategoryId(5L), new BigDecimal("81.34"),
                OperationType.EXPENSE, LocalDateTime.of(2022, Month.JULY,12 , 20, 55, 0,
                0), ActualOrPlanned.ACTUAL, OperationFrequency.NONE),

        new OperationCreateDto("concert tickets", categoryService.findCategoryByCategoryId(5L), new BigDecimal("440.00"),
                OperationType.EXPENSE, LocalDateTime.of(2022, Month.JULY,12 , 20, 26, 0,
                0), ActualOrPlanned.ACTUAL, OperationFrequency.NONE),

        new OperationCreateDto("vod streaming", categoryService.findCategoryByCategoryId(5L), new BigDecimal("40.00"),
                OperationType.EXPENSE, LocalDateTime.of(2022, Month.JULY,21 , 8, 25, 0,
                0), ActualOrPlanned.ACTUAL, OperationFrequency.NONE),

        new OperationCreateDto("music streaming", categoryService.findCategoryByCategoryId(5L), new BigDecimal("30.00"),
                OperationType.EXPENSE, LocalDateTime.of(2022, Month.JULY,21 , 8, 15, 0,
                0), ActualOrPlanned.ACTUAL, OperationFrequency.NONE),

        new OperationCreateDto("fuel", categoryService.findCategoryByCategoryId(4L), new BigDecimal("250.39"),
                OperationType.EXPENSE, LocalDateTime.of(2022, Month.JULY,21 , 8, 15, 0,
                0), ActualOrPlanned.ACTUAL, OperationFrequency.NONE),

        new OperationCreateDto("savings", categoryService.findCategoryByCategoryId(7L), new BigDecimal("100.00"),
                OperationType.SAVING, LocalDateTime.of(2022, Month.JULY,11 , 8, 15, 0,
                0), ActualOrPlanned.ACTUAL, OperationFrequency.NONE),


//august
        new OperationCreateDto("salary", categoryService.findCategoryByCategoryId(1L), new BigDecimal("5320.87"),
                OperationType.EXPENSE, LocalDateTime.of(2022, Month.AUGUST, 10, 20, 40, 0,
                0), ActualOrPlanned.ACTUAL, OperationFrequency.MONTHLY),

        new OperationCreateDto("heating", categoryService.findCategoryByCategoryId(3L), new BigDecimal("120.87"),
                OperationType.EXPENSE, LocalDateTime.of(2022, Month.AUGUST, 14, 20, 40, 0,
                0), ActualOrPlanned.ACTUAL, OperationFrequency.NONE),

        new OperationCreateDto("electricity", categoryService.findCategoryByCategoryId(3L), new BigDecimal("470.35"),
                OperationType.EXPENSE, LocalDateTime.of(2022, Month.AUGUST, 14, 21, 40, 0,
                0), ActualOrPlanned.ACTUAL, OperationFrequency.MONTHLY),

        new OperationCreateDto("water", categoryService.findCategoryByCategoryId(3L), new BigDecimal("257.34"),
                OperationType.EXPENSE, LocalDateTime.of(2022, Month.AUGUST, 14, 21, 45, 0,
                0), ActualOrPlanned.ACTUAL, OperationFrequency.MONTHLY),

        new OperationCreateDto("internet", categoryService.findCategoryByCategoryId(3L), new BigDecimal("99.99"),
                OperationType.EXPENSE, LocalDateTime.of(2022, Month.AUGUST, 19, 10, 55, 0,
                0), ActualOrPlanned.ACTUAL, OperationFrequency.MONTHLY),

        new OperationCreateDto("mobile", categoryService.findCategoryByCategoryId(3L), new BigDecimal("59.99"),
                OperationType.EXPENSE, LocalDateTime.of(2022, Month.AUGUST, 19, 10, 55, 0,
                0), ActualOrPlanned.ACTUAL, OperationFrequency.MONTHLY),

        new OperationCreateDto("flat rent", categoryService.findCategoryByCategoryId(3L), new BigDecimal("2100.00"),
                OperationType.EXPENSE, LocalDateTime.of(2022, Month.AUGUST, 19, 10, 55, 0,
                0), ActualOrPlanned.ACTUAL, OperationFrequency.MONTHLY),

        new OperationCreateDto("food supplies", categoryService.findCategoryByCategoryId(2L), new BigDecimal("210.54"),
                OperationType.EXPENSE, LocalDateTime.of(2022, Month.AUGUST,4 , 18, 55, 0,
                0), ActualOrPlanned.ACTUAL, OperationFrequency.NONE),

        new OperationCreateDto("food supplies", categoryService.findCategoryByCategoryId(2L), new BigDecimal("210.54"),
                OperationType.EXPENSE, LocalDateTime.of(2022, Month.AUGUST,14 , 18, 55, 0,
                0), ActualOrPlanned.ACTUAL, OperationFrequency.NONE),

        new OperationCreateDto("food supplies", categoryService.findCategoryByCategoryId(2L), new BigDecimal("390.30"),
                OperationType.EXPENSE, LocalDateTime.of(2022, Month.AUGUST,27 , 18, 55, 0,
                0), ActualOrPlanned.ACTUAL, OperationFrequency.NONE),

        new OperationCreateDto("eating out", categoryService.findCategoryByCategoryId(2L), new BigDecimal("81.34"),
                OperationType.EXPENSE, LocalDateTime.of(2022, Month.AUGUST,12 , 20, 55, 0,
                0), ActualOrPlanned.ACTUAL, OperationFrequency.NONE),

        new OperationCreateDto("pizza", categoryService.findCategoryByCategoryId(5L), new BigDecimal("47.39"),
                OperationType.EXPENSE, LocalDateTime.of(2022, Month.AUGUST,12 , 20, 26, 0,
                0), ActualOrPlanned.ACTUAL, OperationFrequency.NONE),

        new OperationCreateDto("vod streaming", categoryService.findCategoryByCategoryId(5L), new BigDecimal("40.00"),
                OperationType.EXPENSE, LocalDateTime.of(2022, Month.AUGUST,21 , 8, 25, 0,
                0), ActualOrPlanned.ACTUAL, OperationFrequency.NONE),

        new OperationCreateDto("music streaming", categoryService.findCategoryByCategoryId(5L), new BigDecimal("30.00"),
                OperationType.EXPENSE, LocalDateTime.of(2022, Month.AUGUST,21 , 8, 15, 0,
                0), ActualOrPlanned.ACTUAL, OperationFrequency.NONE),

        new OperationCreateDto("savings", categoryService.findCategoryByCategoryId(4L), new BigDecimal("200"),
                OperationType.EXPENSE, LocalDateTime.of(2022, Month.AUGUST,21 , 8, 15, 0,
                0), ActualOrPlanned.ACTUAL, OperationFrequency.NONE)

        );

        List<Operation> operations = operationCreateDtos.stream()
                .map(this::convertCreateDtoToOperation)
                .toList();

        operations.forEach(operationService::save);

        budget.setOperations(operations);
        budgetService.update(budget);

        String message = "Generated";

        return new ResponseEntity<>(message, HttpStatus.OK);
    }
}
