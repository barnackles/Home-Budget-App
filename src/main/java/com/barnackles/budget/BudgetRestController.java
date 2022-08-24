package com.barnackles.budget;

import com.barnackles.ApplicationSecurity.IAuthenticationFacade;
import com.barnackles.operation.Operation;
import com.barnackles.operation.OperationService;
import com.barnackles.user.User;
import com.barnackles.user.UserService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityExistsException;
import javax.validation.Valid;
import java.util.Comparator;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/budget")
public class BudgetRestController {

    private final BudgetService budgetService;
    private final ModelMapper modelMapper;

    private final UserService userService;
    private final OperationService operationService;

    private final IAuthenticationFacade authenticationFacade;



    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    @GetMapping("/budgets/current-user")
    public ResponseEntity<List<BudgetResponseDtoAll>> findAllUserBudgets() {

       Authentication authentication = authenticationFacade.getAuthentication();

       List<Budget> userBudgetList = userService.findUserByUserName(authentication.getName()).getBudgets();
       List<BudgetResponseDtoAll> listOfBudgetResponseDtos = userBudgetList
                .stream()
                .map(this::convertBudgetToResponseDtoAll)
                .toList();
        return new ResponseEntity<>(listOfBudgetResponseDtos, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    @GetMapping("/budget/{budgetName}")
    public ResponseEntity<BudgetResponseDto> findUserBudgetByName(@PathVariable String budgetName) {

        Authentication authentication = authenticationFacade.getAuthentication();

        User user = userService.findUserByUserName(authentication.getName());
        Budget budget = budgetService.findBudgetByBudgetNameAndUserEquals(budgetName, user);
        List<Operation> recentFiveOperationsByDate = budget.getOperations().stream()
                .sorted(Comparator.comparing(Operation::getOperationDateTime)).toList();

        BudgetResponseDto budgetResponseDto = convertBudgetToResponseDto(budget);
//        budgetResponseDto.setRecentOperations(operationService.findTop5operationAndBudgetEquals(budget.getId()));
//        budgetResponseDto.setRecentFiveOperations(recentFiveOperationsByDate);

        return new ResponseEntity<>(budgetResponseDto, HttpStatus.OK);
    }
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    @PostMapping("/budget")
    public ResponseEntity<BudgetResponseDto> createBudget(@Valid @RequestBody BudgetCreateDto budgetCreateDto) {
        Budget budget = convertCreateDtoToBudget(budgetCreateDto);
        Authentication authentication = authenticationFacade.getAuthentication();
        User user = userService.findUserByUserName(authentication.getName());

        if (!budgetService.checkIfUserHasBudgetWithGivenName(budget.getBudgetName(), user)) {
            budget.setUser(user);
            budgetService.save(budget);

            List<Budget> budgetList = user.getBudgets();
            budgetList.add(budget);
            user.setBudgets(budgetList);
            userService.updateUser(user);
            BudgetResponseDto budgetResponseDto = convertBudgetToResponseDto(budget);


            return new ResponseEntity<>(budgetResponseDto, HttpStatus.CREATED);
        }

        throw new EntityExistsException("Budget with this name already exists.");
    }

    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    @PutMapping("/budget")
    public ResponseEntity<BudgetResponseDto> updateBudget(@Valid @RequestBody BudgetUpdateDto budgetUpdateDto) {

        Authentication authentication = authenticationFacade.getAuthentication();
        User user = userService.findUserByUserName(authentication.getName());

        Budget budget = budgetService.findBudgetByBudgetNameAndUserEquals(budgetUpdateDto.getCurrentBudgetName(), user);
        budget.setBudgetName(budgetUpdateDto.getNewBudgetName());
        budgetService.update(budget);

        BudgetResponseDto budgetResponseDto = convertBudgetToResponseDto(budget);
        return new ResponseEntity<>(budgetResponseDto, HttpStatus.CREATED);

    }

    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    @DeleteMapping("/budget/{budgetName}")
    public ResponseEntity<String> deleteBudget(@PathVariable String budgetName) {
        Authentication authentication = authenticationFacade.getAuthentication();
        User user = userService.findUserByUserName(authentication.getName());

        String message = String.format("Budget: %s successfully deleted ", budgetName);
        budgetService.delete(budgetService.findBudgetByBudgetNameAndUserEquals(budgetName, user));
        return new ResponseEntity<>(message, HttpStatus.OK);
    }


    private Budget convertCreateDtoToBudget(BudgetCreateDto budgetCreateDto) {
        return modelMapper.map(budgetCreateDto, Budget.class);
    }

    private BudgetResponseDto convertBudgetToResponseDto(Budget budget) {
        return modelMapper.map(budget, BudgetResponseDto.class);
    }

    private BudgetResponseDtoAll convertBudgetToResponseDtoAll(Budget budget) {
        return modelMapper.map(budget, BudgetResponseDtoAll.class);
    }

}
