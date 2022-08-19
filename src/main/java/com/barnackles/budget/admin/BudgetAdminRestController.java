package com.barnackles.budget.admin;

import com.barnackles.budget.Budget;
import com.barnackles.budget.BudgetCreateDto;
import com.barnackles.budget.BudgetResponseDto;
import com.barnackles.budget.BudgetService;
import com.barnackles.user.User;
import com.barnackles.user.UserService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/budget")
public class BudgetAdminRestController {

    private final BudgetService budgetService;
    private final ModelMapper modelMapper;
    private final UserService userService;

    //add pagination
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/budgets/all")
    public ResponseEntity<List<BudgetResponseDto>> findAll() {
        List<Budget> budgets = budgetService.findAll();
        List<BudgetResponseDto> listOfBudgetResponseDtos = budgets
                .stream()
                .map(this::convertBudgetResponseDto)
                .toList();
        return new ResponseEntity<>(listOfBudgetResponseDtos, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/budget/{id}")
    public ResponseEntity<BudgetResponseDto> createBudgetForUser(@RequestBody BudgetCreateDto budgetCreateDto, @PathVariable Long id) {
        Budget budget = convertCreateDtoToBudget(budgetCreateDto);


        User user = userService.findUserById(id);
        budget.setUser(user);
        budgetService.save(budget);

        List<Budget> budgetList = user.getBudgets();
        budgetList.add(budget);
        user.setBudgets(budgetList);
        userService.updateUser(user);

        BudgetResponseDto budgetResponseDto = convertBudgetResponseDto(budget);
        return new ResponseEntity<>(budgetResponseDto, HttpStatus.CREATED);
    }


    private Budget convertCreateDtoToBudget(BudgetCreateDto budgetCreateDto) {
        return modelMapper.map(budgetCreateDto, Budget.class);
    }

    private BudgetResponseDto convertBudgetResponseDto(Budget budget) {
        return modelMapper.map(budget, BudgetResponseDto.class);
    }

}
