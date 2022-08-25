package com.barnackles.budget;

import com.barnackles.ApplicationSecurity.IAuthenticationFacade;
import com.barnackles.budget.admin.BudgetOverviewDto;
import com.barnackles.operation.Operation;
import com.barnackles.user.User;
import com.barnackles.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityExistsException;
import javax.validation.Valid;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Comparator;
import java.util.InputMismatchException;
import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/budget")
public class BudgetRestController {

    private final BudgetService budgetService;
    private final ModelMapper modelMapper;
    private final UserService userService;
    private final IAuthenticationFacade authenticationFacade;



    @Secured("ROLE_USER")
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

    @Secured("ROLE_USER")
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
    @Secured("ROLE_USER")
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

    @Secured("ROLE_USER")
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

    @Secured("ROLE_USER")
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

    /**
     * Show actual budget standing
     * @param budgetName
     * @return budgetOverviewDto
     */
    @Secured("ROLE_USER")
    @GetMapping("/budget/overview/all/{budgetName}")
    public ResponseEntity<BudgetOverviewDto> showBudgetOverview(@PathVariable String budgetName) {

        Authentication authentication = authenticationFacade.getAuthentication();
        User user = userService.findUserByUserName(authentication.getName());

        if (budgetService.checkIfUserHasBudgetWithGivenName(budgetName, user)) {
            Budget budget = budgetService.findBudgetByBudgetNameAndUserEquals(budgetName, user);
            return getBudgetOverviewDtoResponseEntity(budgetName, user, budget.getOperations());
        } else {
            throw new AccessDeniedException("Permission denied.");
        }
    }

        /**
         * Show actual budget standing for last week
         * @param budgetName
         * @return budgetOverviewDto
         */

    @Secured("ROLE_USER")
    @GetMapping("/budget/overview/week/{budgetName}")
    public ResponseEntity<BudgetOverviewDto> showBudgetOverviewForLastWeek(@PathVariable String budgetName) {

        Authentication authentication = authenticationFacade.getAuthentication();
        User user = userService.findUserByUserName(authentication.getName());

        if (budgetService.checkIfUserHasBudgetWithGivenName(budgetName, user)) {
            Budget budget = budgetService.findBudgetByBudgetName(budgetName);

            List<Operation> lastWeekOperations = budget.getOperations().stream()
                    .filter(op -> op.getOperationDateTime().isAfter((LocalDateTime.now().minusDays(7L))))
                    .toList();
            return getBudgetOverviewDtoResponseEntity(budgetName, user, lastWeekOperations);
        } else {
            throw new AccessDeniedException("Permission denied.");
        }
    }




    /**
     * Show actual budget standing for last month
     * @param budgetName
     * @return budgetOverviewDto
     */

    @Secured("ROLE_USER")
    @GetMapping("/budget/overview/month/{budgetName}")
    public ResponseEntity<BudgetOverviewDto> showBudgetOverviewForLastMonth(@PathVariable String budgetName){

        Authentication authentication = authenticationFacade.getAuthentication();
        User user = userService.findUserByUserName(authentication.getName());

        if (budgetService.checkIfUserHasBudgetWithGivenName(budgetName, user)) {

            Budget budget = budgetService.findBudgetByBudgetName(budgetName);
            List<Operation> lastMonthOperations = budget.getOperations().stream()
                    .filter(op -> op.getOperationDateTime().isAfter((LocalDateTime.now().minusMonths(1L)))).toList();

            return getBudgetOverviewDtoResponseEntity(budgetName, user, lastMonthOperations);
        } else {
            throw new AccessDeniedException("Permission denied.");
        }
}
    /**
         * Show actual budget standing for last year
         * @param budgetName
         * @return budgetOverviewDto
         */


    @Secured("ROLE_USER")
    @GetMapping("/budget/overview/year/{budgetName}")
    public ResponseEntity<BudgetOverviewDto> showBudgetOverviewForLastYear(@PathVariable String budgetName){

        Authentication authentication = authenticationFacade.getAuthentication();
        User user = userService.findUserByUserName(authentication.getName());

        if (budgetService.checkIfUserHasBudgetWithGivenName(budgetName, user)) {

            Budget budget = budgetService.findBudgetByBudgetName(budgetName);
            List<Operation> lastYearOperations = budget.getOperations().stream()
                    .filter(op -> op.getOperationDateTime().isAfter((LocalDateTime.now().minusYears(1L)))).toList();
            return getBudgetOverviewDtoResponseEntity(budgetName, user, lastYearOperations);
        } else {
            throw new AccessDeniedException("Permission denied.");
        }
    }

    /**
     * Show actual budget standing for custom range of dates
     * @param budgetName
     * @return budgetOverviewDto
     */


    @Secured("ROLE_USER")
    @GetMapping("/budget/overview/custom-dates/{budgetName}/{beginStr}/{endStr}")
    public ResponseEntity<BudgetOverviewDto> showBudgetOverviewForCustomRange(@PathVariable String budgetName,
                                    @PathVariable String beginStr, @PathVariable String endStr){

        Authentication authentication = authenticationFacade.getAuthentication();
        User user = userService.findUserByUserName(authentication.getName());

        if (budgetService.checkIfUserHasBudgetWithGivenName(budgetName, user)) {
            try {
            LocalDate beginDate = LocalDate.parse(beginStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            LocalDate endDate = LocalDate.parse(endStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"));

            LocalDateTime begin = beginDate.atStartOfDay();
            LocalDateTime end = endDate.atTime(23, 59,59);

                Budget budget = budgetService.findBudgetByBudgetName(budgetName);
                List<Operation> customDateRangeOperations = budget.getOperations().stream()
                        .filter(op -> (
                                op.getOperationDateTime().isAfter(begin) && op.getOperationDateTime().isBefore(end)))
                        .toList();

                return getBudgetOverviewDtoResponseEntity(budgetName, user, customDateRangeOperations);
            } catch (DateTimeParseException e) {
                log.error("LocalDate parsing error: {}", e.getMessage());
                throw new InputMismatchException("Incorrect date format. Date should be in format: yyyy-mm-dd");
            }

        } else {
            throw new AccessDeniedException("Permission denied.");
        }
    }


    //mappers

    private ResponseEntity<BudgetOverviewDto> getBudgetOverviewDtoResponseEntity(@PathVariable String budgetName, User user, List<Operation> operations) {
        BudgetOverviewDto budgetOverviewDto = new BudgetOverviewDto();
        budgetOverviewDto.setUserName(user.getUserName());
        budgetOverviewDto.setBudgetName(budgetName);
        budgetOverviewDto.setBalance(budgetService.calculateBalance(operations));
        budgetOverviewDto.setIncome(budgetService.calculateIncome(operations));
        budgetOverviewDto.setExpenses(budgetService.calculateExpense(operations));
        budgetOverviewDto.setSavings(budgetService.calculateSavings(operations));

        return new ResponseEntity<>(budgetOverviewDto, HttpStatus.OK);
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
