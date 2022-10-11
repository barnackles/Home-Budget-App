package com.barnackles.startup;

import com.barnackles.category.Category;
import com.barnackles.category.CategoryService;
import com.barnackles.role.Role;
import com.barnackles.role.RoleService;
import com.barnackles.user.User;
import com.barnackles.user.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class CommandLineStartupRunner implements CommandLineRunner {

    public static final String ROLE_USER = "ROLE_USER";
    public static final String ROLE_ADMIN = "ROLE_ADMIN";
    private final RoleService roleService;
    private final UserServiceImpl userService;
    private final CategoryService categoryService;

    @Value("${admin.username}")
    private String username;
    @Value("${admin.email}")
    private String email;
    @Value("${admin.password}")
    private String password;



    @Override
    public void run(String... args) throws Exception {

        if((roleService.findByRole(ROLE_USER) == null) && (roleService.findByRole(ROLE_ADMIN) == null)) {
            createRoles();
        }
        if(userService.findUserByEmailOpt(email).isEmpty()) {
            createAdminAccount(username, email, password);
        }
        if(categoryService.findAll().isEmpty()) {
            createCategories();
        }



    }
    private void createRoles() {

        Role roleUser = new Role();
        roleUser.setRole(ROLE_USER);
        roleService.saveRole(roleUser);

        Role roleAdmin = new Role();
        roleAdmin.setRole(ROLE_ADMIN);
        roleService.saveRole(roleAdmin);

        log.info("Roles : {}, {} created.", roleService.findByRole(ROLE_USER), roleService.findByRole(ROLE_USER));
    }

    private void createAdminAccount(String username, String email, String password) {

        User admin = new User();
        admin.setUserName(username);
        admin.setEmail(email);
        admin.setPassword(password);
        userService.saveUser(admin);
        log.info("Created user: {}", userService.findUserByUserName("jasonAdmin").getUserName());

        // set admin privileges
        User persistentAdmin = userService.findUserByUserName("jasonAdmin");
        Role adminRole = roleService.findByRole(ROLE_ADMIN);
        persistentAdmin.setRoles(new HashSet<>(Collections.singletonList(adminRole)));
        userService.updateUser(persistentAdmin);
        log.info("User roles: {}", userService.findUserByUserName("jasonAdmin").getRoles().toString());

    }

    private void createCategories() {

    Category salary = new Category();
    salary.setName("salary");

    Category shopping = new Category();
    shopping.setName("shopping");

    Category housingAndBilling = new Category();
    housingAndBilling.setName("housing and billing");

    Category transportation = new Category();
    transportation.setName("transportation");

    Category entertainment = new Category();
    entertainment.setName("entertainment");

    Category saving = new Category();
    saving.setName("saving");

    List<Category> initialCategories = List.of(
      salary, shopping, housingAndBilling, transportation, entertainment, saving
    );

    initialCategories.forEach(categoryService::save);
    log.info("Initial categories: {}", categoryService.findAll().toString());

    }
}
