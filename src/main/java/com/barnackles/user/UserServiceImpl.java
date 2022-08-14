package com.barnackles.user;

import com.barnackles.role.Role;
import com.barnackles.role.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final BCryptPasswordEncoder passwordEncoder;


    public User findUserByEmail(String email) throws EntityNotFoundException {
        log.info("User found: {}", email);
        return userRepository.findUserByEmail(email).orElseThrow(
                () -> {
                    log.error("entity not found with email: {} not found", email);
                    throw new EntityNotFoundException("entity not found");
                }
        );
    }

    public User findUserByUserName(String userName) throws EntityNotFoundException {
        log.info("User found: {}", userName);
        return userRepository.findUserByUserName(userName).orElseThrow(
                () -> {
                    log.error("entity not found with userName: {} not found", userName);
                    throw new EntityNotFoundException("entity not found");
                }
        );
    }

    public User findUserById(Long id) throws EntityNotFoundException {
        log.info("User found: {}", id);
        return userRepository.findUserById(id).orElseThrow(
                () -> {
                    log.error("entity not found with id: {} not found", id);
                    throw new EntityNotFoundException("entity not found");
                }
        );
    }

    @Override
    public List<User> findAll() {
        log.info("All users found");
        return userRepository.findAll();
    }

    public User saveUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setActive(true);
        Role userRole = roleRepository.findByRole("ROLE_USER");
        user.setRoles(new HashSet<Role>(Arrays.asList(userRole)));
        log.info("User saved: {}", user);
        userRepository.save(user);
        return user;
    }

    public User updateUser(User user) {
        log.info("User updated: {}", user);
        userRepository.save(user);
        return user;
    }

    public User updateUserPassword(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        log.info("Password for user: {} successfully updated", user.getUserName());
        return user;
    }

    public void deleteUser(User user) {
        log.info("User deleted: {}", user.getUserName());
        userRepository.deleteUserById(user.getId());
    }

}
