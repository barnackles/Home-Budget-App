package com.barnackles.budget.admin;

import com.barnackles.budget.Budget;
import com.barnackles.budget.BudgetCreateDto;
import com.barnackles.budget.BudgetService;
import com.barnackles.operation.Operation;
import com.barnackles.user.User;
import com.barnackles.user.UserService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.persistence.EntityExistsException;
import javax.validation.Valid;
import java.util.Comparator;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/budget")
@ApiIgnore
public class BudgetAdminRestController {

    private final BudgetService budgetService;
    private final ModelMapper modelMapper;
    private final UserService userService;



    @Secured("ROLE_ADMIN")
    @GetMapping("/budgets/all")
    public ResponseEntity<List<BudgetAdminResponseDtoAll>> findAll() {

        List<Budget> budgets = budgetService.findAll();
        List<BudgetAdminResponseDtoAll> listOfBudgetAdminResponseDtos = budgets
                .stream()
                .map(this::convertToBudgetAdminResponseDtoAll)
                .toList();
        return new ResponseEntity<>(listOfBudgetAdminResponseDtos, HttpStatus.OK);
    }

    @Secured("ROLE_ADMIN")
    @GetMapping("/budgets/all/{pageNumber}/{pageSize}/{sortBy}")
    public ResponseEntity<List<BudgetAdminResponseDtoAll>> findAll(@PathVariable int pageNumber,
                                                                   @PathVariable int pageSize, @PathVariable String sortBy) {

        List<Budget> budgets = budgetService.findAll(pageNumber, pageSize, sortBy);
        List<BudgetAdminResponseDtoAll> listOfBudgetAdminResponseDtos = budgets
                .stream()
                .map(this::convertToBudgetAdminResponseDtoAll)
                .toList();
        return new ResponseEntity<>(listOfBudgetAdminResponseDtos, HttpStatus.OK);
    }

    @Secured("ROLE_ADMIN")
    @GetMapping("/budget/budget-id/{budgetId}")
    public ResponseEntity<BudgetAdminResponseDto> findBudgetById(@PathVariable Long budgetId) {

        Budget budget = budgetService.findBudgetByBudgetId(budgetId);
        BudgetAdminResponseDto budgetAdminResponseDto = new BudgetAdminResponseDto(); // convertToBudgetAdminResponseDto(budget);

        List<Operation> recentFiveOperationsByDate = budget.getOperations().stream()
                .sorted(Comparator.comparing(Operation::getOperationDateTime)).toList();

        budgetAdminResponseDto.setId(budget.getId());
        budgetAdminResponseDto.setBudgetName(budget.getBudgetName());
        budgetAdminResponseDto.setUserId(budget.getUser().getId());
        budgetAdminResponseDto.setUserName(budget.getUser().getUserName());

        budgetAdminResponseDto.setRecentFiveOperations(recentFiveOperationsByDate);

        return new ResponseEntity<>(budgetAdminResponseDto, HttpStatus.OK);
    }

    @Secured("ROLE_ADMIN")
    @PostMapping("/budget/user-id/{userId}")
    public ResponseEntity<BudgetAdminResponseDto> createBudgetForUser(@Valid @RequestBody BudgetCreateDto budgetCreateDto,
                                                                      @PathVariable Long userId) {
        Budget budget = convertCreateDtoToBudget(budgetCreateDto);
        User user = userService.findUserById(userId);

        if (!budgetService.checkIfUserHasBudgetWithGivenName(budget.getBudgetName(), user)) {
            budget.setUser(user);
            budgetService.save(budget);

            List<Budget> budgetList = user.getBudgets();
            budgetList.add(budget);
            user.setBudgets(budgetList);
            userService.updateUser(user);

            BudgetAdminResponseDto budgetAdminResponseDto = convertToBudgetAdminResponseDto(budget);
            return new ResponseEntity<>(budgetAdminResponseDto, HttpStatus.CREATED);

        }

        throw new EntityExistsException("Budget with this name already exists.");
    }

    @Secured("ROLE_ADMIN")
    @PutMapping("/budget")
    public ResponseEntity<BudgetAdminResponseDto> updateBudget(@Valid @RequestBody BudgetAdminUpdateDto budgetAdminUpdateDto) {

        Budget budget = budgetService.findBudgetByBudgetId(budgetAdminUpdateDto.getId());
        budget.setBudgetName(budgetAdminUpdateDto.getNewBudgetName());
        budgetService.update(budget);

        BudgetAdminResponseDto budgetAdminResponseDto = convertToBudgetAdminResponseDto(budget);
        return new ResponseEntity<>(budgetAdminResponseDto, HttpStatus.OK);

    }

    @Secured("ROLE_ADMIN")
    @DeleteMapping("/budget/{budgetId}")
    public ResponseEntity<String> deleteBudget(@PathVariable Long budgetId) {

        String message = String.format("Budget of id: %d successfully deleted ", budgetId);
        budgetService.delete(budgetService.findBudgetByBudgetId(budgetId));
        return new ResponseEntity<>(message, HttpStatus.OK);
    }

    private Budget convertCreateDtoToBudget(BudgetCreateDto budgetCreateDto) {
        return modelMapper.map(budgetCreateDto, Budget.class);
    }

    private BudgetAdminResponseDto convertToBudgetAdminResponseDto(Budget budget) {
        return modelMapper.map(budget, BudgetAdminResponseDto.class);
    }

    private BudgetAdminResponseDtoAll convertToBudgetAdminResponseDtoAll(Budget budget) {
        return modelMapper.map(budget, BudgetAdminResponseDtoAll.class);
    }

}
