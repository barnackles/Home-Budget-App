package com.barnackles.operation;

import com.barnackles.ApplicationSecurity.IAuthenticationFacade;
import com.barnackles.budget.Budget;
import com.barnackles.budget.BudgetService;
import com.barnackles.user.User;
import com.barnackles.user.UserService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
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



    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    @GetMapping("/operations/budget/all/{budgetName}")
    public ResponseEntity<List<OperationResponseDto>> findAllBudgetOperations(
            @PathVariable String budgetName) {

        Authentication authentication = authenticationFacade.getAuthentication();
        User user = userService.findUserByUserName(authentication.getName());
        Budget budget = budgetService.findBudgetByBudgetNameAndUserEquals(budgetName, user);

        List<Operation> operations = budget.getOperations();
        List<OperationResponseDto> listOfOperationResponseDtos = operations
                .stream()
                .map(this::convertToOperationResponseDto)
                .toList();
        return new ResponseEntity<>(listOfOperationResponseDtos, HttpStatus.OK);
    }


    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    @GetMapping("/operations/budget/all/{budgetName}/{pageNumber}/{pageSize}/{sortBy}")
    public ResponseEntity<List<OperationResponseDto>> findAllBudgetOperations(
            @PathVariable String budgetName, @PathVariable int pageNumber, @PathVariable int pageSize,
            @PathVariable String sortBy) {

        Authentication authentication = authenticationFacade.getAuthentication();
        User user = userService.findUserByUserName(authentication.getName());
        Budget budget = budgetService.findBudgetByBudgetNameAndUserEquals(budgetName, user);

        List<Operation> operations = operationService.findAllOperationsForBudget(pageNumber,
                pageSize, sortBy, budget);
        List<OperationResponseDto> listOfOperationResponseDtos = operations
                .stream()
                .map(this::convertToOperationResponseDto)
                .toList();

        return new ResponseEntity<>(listOfOperationResponseDtos, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    @GetMapping ("/operation/{operationUuid}")
    public ResponseEntity<OperationResponseDto> findOperationByUuid(@PathVariable String operationUuid) {
        UUID uuid = UUID.fromString(operationUuid);

        Authentication authentication = authenticationFacade.getAuthentication();
        Operation operation = operationService.findOperationByOperationUuid(uuid);
        User user = userService.findUserByUserName(authentication.getName());

        if (budgetService.checkIfUserHasBudgetWithGivenName(operation.getBudget().getBudgetName(), user)) {
            OperationResponseDto operationResponseDto = convertToOperationResponseDto(operation);
            return new ResponseEntity<>(operationResponseDto, HttpStatus.OK);
        } else {
            throw new AccessDeniedException("Permission denied.");
        }

    }

    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    @PostMapping("/operation/{budgetName}")
    public ResponseEntity<OperationResponseDto> createOperationForBudget(@Valid @RequestBody OperationCreateDto operationCreateDto,
                                                                              @PathVariable String budgetName) {
        Authentication authentication = authenticationFacade.getAuthentication();
        User user = userService.findUserByUserName(authentication.getName());

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

    }

    private Operation convertCreateDtoToOperation(OperationCreateDto operationCreateDto) {
        return modelMapper.map(operationCreateDto, Operation.class);
    }

    private OperationResponseDto convertToOperationResponseDto(Operation operation) {
        return modelMapper.map(operation, OperationResponseDto.class);
    }
}
