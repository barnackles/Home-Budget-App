package com.barnackles.budget;

import com.barnackles.ApplicationSecurity.IAuthenticationFacade;
import com.barnackles.user.User;
import com.barnackles.user.UserService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/budget")
public class BudgetRestController {

    private final BudgetService budgetService;
    private final ModelMapper modelMapper;

    private final UserService userService;

    private final IAuthenticationFacade authenticationFacade;


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
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/budgets/current-user")
    public ResponseEntity<List<BudgetResponseDto>> findAllUserBudgets() {

       Authentication authentication = authenticationFacade.getAuthentication();

       List<Budget> userBudgetList = userService.findUserByUserName(authentication.getName()).getBudgets();
       List<BudgetResponseDto> listOfBudgetResponseDtos = userBudgetList
                .stream()
                .map(this::convertBudgetResponseDto)
                .toList();
        return new ResponseEntity<>(listOfBudgetResponseDtos, HttpStatus.OK);
    }

    //findbudgetbyname

    @PostMapping("/budget")
    public ResponseEntity<BudgetResponseDto> createBudget(@RequestBody BudgetCreateDto budgetCreateDto) {
        Budget budget = convertCreateDtoToBudget(budgetCreateDto);
        Authentication authentication = authenticationFacade.getAuthentication();

        User user = userService.findUserByUserName(authentication.getName());

        List<User> usersList = new ArrayList<>();
        usersList.add(user);
        budget.setUsers(usersList);
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
