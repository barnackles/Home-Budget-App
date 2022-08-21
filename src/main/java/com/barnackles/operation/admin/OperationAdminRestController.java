package com.barnackles.operation.admin;

import com.barnackles.operation.OperationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/operation")
public class OperationAdminRestController {

    private final OperationService operationService;


}
