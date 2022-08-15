package com.barnackles.budget;

import com.barnackles.user.User;
import com.barnackles.user.UserService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/budget")
public class BudgetRestController {

    private final BudgetService budgetService;
    private final ModelMapper modelMapper;

    private final UserService userService;






    @PostMapping("/budget")
    public ResponseEntity<BudgetResponseDto> createBudget(@RequestBody BudgetCreateDto budgetCreateDto) {
        Budget budget = convertCreateDtoToBudget(budgetCreateDto);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();
        User user = userService.findUserByUserName(currentPrincipalName);
        List<User> usersList = new ArrayList<>();
        usersList.add(user);
        budget.setUsers(usersList);
        budgetService.save(budget);

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
