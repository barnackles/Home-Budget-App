package com.barnackles.operation;

import com.barnackles.ApplicationSecurity.IAuthenticationFacade;
import com.barnackles.budget.Budget;
import com.barnackles.budget.BudgetService;
import com.barnackles.user.User;
import com.barnackles.user.UserService;
import com.fasterxml.uuid.impl.UUIDUtil;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/operation")
public class OperationRestController {

    private final OperationService operationService;

    private final UserService userService;
    private final BudgetService budgetService;
    private final ModelMapper modelMapper;

    private final IAuthenticationFacade authenticationFacade;


    @Secured("ROLE_USER")
    @GetMapping("/operations/budget/all/{budgetName}")
    public ResponseEntity<List<OperationResponseDto>> findAllBudgetOperations(
            @PathVariable String budgetName) {

        Authentication authentication = authenticationFacade.getAuthentication();
        User user = userService.findUserByUserName(authentication.getName());

        if (budgetService.checkIfUserHasBudgetWithGivenName(budgetName, user)) {
            Budget budget = budgetService.findBudgetByBudgetNameAndUserEquals(budgetName, user);
            List<Operation> operations = budget.getOperations();
            List<OperationResponseDto> listOfOperationResponseDtos = operations
                    .stream()
                    .map(this::convertToOperationResponseDto)
                    .toList();

            return new ResponseEntity<>(listOfOperationResponseDtos, HttpStatus.OK);
        } else {
            throw new AccessDeniedException("Permission denied.");
        }
    }


    @Secured("ROLE_USER")
    @GetMapping("/operations/budget/all/{budgetName}/{pageNumber}/{pageSize}/{sortBy}")
    public ResponseEntity<List<OperationResponseDto>> findAllBudgetOperations(
            @PathVariable String budgetName, @PathVariable int pageNumber, @PathVariable int pageSize,
            @PathVariable String sortBy) {

        Authentication authentication = authenticationFacade.getAuthentication();
        User user = userService.findUserByUserName(authentication.getName());

        if (budgetService.checkIfUserHasBudgetWithGivenName(budgetName, user)) {
            Budget budget = budgetService.findBudgetByBudgetNameAndUserEquals(budgetName, user);

            List<Operation> operations = operationService.findAllOperationsForBudget(pageNumber,
                    pageSize, sortBy, budget);
            List<OperationResponseDto> listOfOperationResponseDtos = operations
                    .stream()
                    .map(this::convertToOperationResponseDto)
                    .toList();

            return new ResponseEntity<>(listOfOperationResponseDtos, HttpStatus.OK);
        } else {
            throw new AccessDeniedException("Permission denied.");
        }

    }

    @Secured("ROLE_USER")
    @GetMapping("/operation/{operationUuid}")
    public ResponseEntity<OperationResponseDto> findOperationByUuid(@PathVariable String operationUuid) {
        UUID uuidFromStr = UUIDUtil.uuid(operationUuid);

        Authentication authentication = authenticationFacade.getAuthentication();
        Operation operation = operationService.findOperationByOperationUuid(uuidFromStr);
        User user = userService.findUserByUserName(authentication.getName());

        if (budgetService.checkIfUserHasBudgetWithGivenName(operation.getBudget().getBudgetName(), user)) {
            OperationResponseDto operationResponseDto = convertToOperationResponseDto(operation);
            return new ResponseEntity<>(operationResponseDto, HttpStatus.OK);
        } else {
            throw new AccessDeniedException("Permission denied.");
        }

    }

    @Secured("ROLE_USER")
    @PostMapping("/operation/{budgetName}")
    public ResponseEntity<OperationResponseDto> createOperationForBudget(@Valid @RequestBody OperationCreateDto operationCreateDto,
                                                                         @PathVariable String budgetName) {
        Authentication authentication = authenticationFacade.getAuthentication();
        User user = userService.findUserByUserName(authentication.getName());

        if (budgetService.checkIfUserHasBudgetWithGivenName(budgetName, user)) {

            Budget budget = budgetService.findBudgetByBudgetNameAndUserEquals(budgetName, user);
            Operation operation = convertCreateDtoToOperation(operationCreateDto);
            operation.setBudget(budget);
            operationService.save(operation);

            List<Operation> operationList = budget.getOperations();
            operationList.add(operation);
            budget.setOperations(operationList);
            budgetService.update(budget);

            OperationResponseDto operationResponseDto = convertToOperationResponseDto(operation);
            operationResponseDto.setBudgetName(budget.getBudgetName());
            return new ResponseEntity<>(operationResponseDto, HttpStatus.CREATED);

        } else {
            throw new AccessDeniedException("Permission denied.");
        }

    }

    @Secured("ROLE_USER")
    @PutMapping("/operation/{operationUuid}")
    public ResponseEntity<OperationResponseDto> updateOperation(@Valid @RequestBody OperationCreateDto operationCreateDto,
                                                                @PathVariable String operationUuid) {

        UUID uuidFromStr = UUIDUtil.uuid(operationUuid);

        Authentication authentication = authenticationFacade.getAuthentication();
        Operation persistentOperation = operationService.findOperationByOperationUuid(uuidFromStr);
        User user = userService.findUserByUserName(authentication.getName());

        if (budgetService.checkIfUserHasBudgetWithGivenName(persistentOperation.getBudget().getBudgetName(), user)) {

            Operation operation = convertCreateDtoToOperation(operationCreateDto);
            operation.setId(persistentOperation.getId());
            operation.setUuid(persistentOperation.getUuid());
            operation.setBudget(persistentOperation.getBudget());

            operationService.update(operation);

            OperationResponseDto operationResponseDto = convertToOperationResponseDto(persistentOperation);
            return new ResponseEntity<>(operationResponseDto, HttpStatus.OK);
        } else {
            throw new AccessDeniedException("Permission denied.");
        }

    }

    @Secured("ROLE_USER")
    @DeleteMapping("/operation/{operationUuid}")
    public ResponseEntity<String> deleteOperation(@PathVariable String operationUuid) {

        UUID uuidFromStr = UUIDUtil.uuid(operationUuid);
        Authentication authentication = authenticationFacade.getAuthentication();
        Operation persistentOperation = operationService.findOperationByOperationUuid(uuidFromStr);
        User user = userService.findUserByUserName(authentication.getName());

        if (budgetService.checkIfUserHasBudgetWithGivenName(persistentOperation.getBudget().getBudgetName(), user)) {

            operationService.delete(persistentOperation);
            String message = String.format("Operation number: %s successfully deleted ", operationUuid);

            return new ResponseEntity<>(message, HttpStatus.OK);
        } else {
            throw new AccessDeniedException("Permission denied.");
        }

    }


    private Operation convertCreateDtoToOperation(OperationCreateDto operationCreateDto) {
        return modelMapper.map(operationCreateDto, Operation.class);
    }

    private OperationResponseDto convertToOperationResponseDto(Operation operation) {
        return modelMapper.map(operation, OperationResponseDto.class);
    }
}
