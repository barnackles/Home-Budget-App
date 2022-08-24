package com.barnackles.budget;

import com.barnackles.ApplicationSecurity.IAuthenticationFacade;
import com.barnackles.budget.admin.BudgetOverviewDto;
import com.barnackles.operation.Operation;
import com.barnackles.operation.OperationService;
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

        if (budgetService.checkIfUserHasBudgetWithGivenName(budgetName, user)) {

        Budget budget = budgetService.findBudgetByBudgetNameAndUserEquals(budgetName, user);
        List<Operation> recentFiveOperationsByDate = budget.getOperations().stream()
                .sorted(Comparator.comparing(Operation::getOperationDateTime)).toList();

        BudgetResponseDto budgetResponseDto = convertBudgetToResponseDto(budget);
        budgetResponseDto.setRecentOperations(recentFiveOperationsByDate);

        return new ResponseEntity<>(budgetResponseDto, HttpStatus.OK);

        } else {
            throw new AccessDeniedException("Permission denied.");
        }
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

        if (budgetService.checkIfUserHasBudgetWithGivenName(budgetUpdateDto.getCurrentBudgetName(), user)) {
            Budget budget = budgetService.findBudgetByBudgetNameAndUserEquals(budgetUpdateDto.getCurrentBudgetName(), user);
            budget.setBudgetName(budgetUpdateDto.getNewBudgetName());
            budgetService.update(budget);

            BudgetResponseDto budgetResponseDto = convertBudgetToResponseDto(budget);
            return new ResponseEntity<>(budgetResponseDto, HttpStatus.OK);

        } else {
            throw new AccessDeniedException("Permission denied.");
        }

    }

    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    @DeleteMapping("/budget/{budgetName}")
    public ResponseEntity<String> deleteBudget(@PathVariable String budgetName) {
        Authentication authentication = authenticationFacade.getAuthentication();
        User user = userService.findUserByUserName(authentication.getName());

        if (budgetService.checkIfUserHasBudgetWithGivenName(budgetName, user)) {

        budgetService.delete(budgetService.findBudgetByBudgetNameAndUserEquals(budgetName, user));
        String message = String.format("Budget: %s successfully deleted ", budgetName);
        return new ResponseEntity<>(message, HttpStatus.OK);

        } else {
            throw new AccessDeniedException("Permission denied.");
        }
    }

    // financial methods

    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    @GetMapping("/budget/overview/all/{budgetName}")
    public ResponseEntity<BudgetOverviewDto> showBudgetOverview(@PathVariable String budgetName) {

        Authentication authentication = authenticationFacade.getAuthentication();

        User user = userService.findUserByUserName(authentication.getName());

        if (budgetService.checkIfUserHasBudgetWithGivenName(budgetName, user)) {

            Budget budget = budgetService.findBudgetByBudgetNameAndUserEquals(budgetName, user);

            BudgetOverviewDto budgetOverviewDto = new BudgetOverviewDto();
            budgetOverviewDto.setUserName(user.getUserName());
            budgetOverviewDto.setBudgetName(budgetName);
            budgetOverviewDto.setBudgetBalance(budgetService.calculateBudgetBalance(budget.getOperations()));
            budgetOverviewDto.setTotalIncome(budgetService.calculateTotalIncome(budget.getOperations()));
            budgetOverviewDto.setTotalExpense(budgetService.calculateTotalExpense(budget.getOperations()));
            budgetOverviewDto.setTotalSavings(budgetService.calculateTotalSavings(budget.getOperations()));


            return new ResponseEntity<>(budgetOverviewDto, HttpStatus.OK);
        } else {
            throw new AccessDeniedException("Permission denied.");
    }

}




    //mappers

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
