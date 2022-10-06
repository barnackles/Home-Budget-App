package com.barnackles.role;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/role")
public class RoleRestController {

    private final RoleService roleService;


    @Secured("ROLE_ADMIN")
    @GetMapping("/roles/{role}")
    public Role findByRole(@PathVariable String role) {
        return roleService.findByRole(role);
    }

    @Secured("ROLE_ADMIN")
    @PostMapping("/roles")
    public void save(@RequestBody Role role) {
        roleService.saveRole(role);
    }

    @Secured("ROLE_ADMIN")
    @PutMapping("/roles/{role}")
    public void update(@PathVariable Role role) {
        roleService.updateRole(role);
    }

    @Secured("ROLE_ADMIN")
    @DeleteMapping("/roles")
    public void delete(@RequestBody Role role) {
        roleService.deleteRole(role);
    }

}
