package com.barnackles.home;

import com.barnackles.ApplicationSecurity.IAuthenticationFacade;
import com.barnackles.budget.BudgetService;
import com.barnackles.operation.OperationService;
import com.barnackles.user.UserService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/home")
public class HomeRestController {

    private final IAuthenticationFacade authenticationFacade;
    private final UserService userService;
    private final BudgetService budgetService;
    private final ModelMapper modelMapper;
    private final OperationService operationService;







}
