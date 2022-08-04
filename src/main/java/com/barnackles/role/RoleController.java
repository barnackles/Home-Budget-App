package com.barnackles.role;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;


    @GetMapping("/roles/{role}")
    public Role findByRole (@PathVariable String role) {
        return roleService.findByRole(role);
    }



    @PostMapping("/roles")
    public void save(@RequestBody Role role) {
        roleService.saveRole(role);
    }

}
