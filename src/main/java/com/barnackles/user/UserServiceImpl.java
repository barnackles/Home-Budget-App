package com.barnackles.user;

import com.barnackles.confirmationToken.ConfirmationToken;
import com.barnackles.confirmationToken.ConfirmationTokenService;
import com.barnackles.email.EmailSender;
import com.barnackles.role.Role;
import com.barnackles.role.RoleRepository;
import com.barnackles.task.DeleteUnconfirmedAccountTask;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    private final ConfirmationTokenService confirmationTokenService;
    private final BCryptPasswordEncoder passwordEncoder;

    private final EmailSender emailSender;

    private final ThreadPoolTaskScheduler scheduler;



    public User findUserByEmail(String email) throws EntityNotFoundException {
        String emailToLowerCase = email.toLowerCase();
        log.info("User found: {}", emailToLowerCase);
        return userRepository.findUserByEmail(emailToLowerCase).orElseThrow(
                () -> {
                    log.error("entity with email: {} not found", emailToLowerCase);
                    throw new EntityNotFoundException("entity not found");
                }
        );
    }

    public User findUserByUserName(String userName) throws EntityNotFoundException {
        log.info("User found: {}", userName);
        return userRepository.findUserByUserName(userName).orElseThrow(
                () -> {
                    log.error("entity with userName: {} not found", userName);
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

    @Override
    public List<User> findAll(int pageNumber, int pageSize, String sortBy) {
        pageNumber -= 1;
        Pageable paging = PageRequest.of(pageNumber, pageSize, Sort.by(sortBy));
        Page<User> pagedResult = userRepository.findAll(paging);
        if (pagedResult.hasContent()) {
            log.info("Users for pageNumber: {}, pageSize: {}, sorted by: {} found.", pageNumber, pageSize, sortBy);
            return pagedResult.getContent();
        } else {
            log.info("No results found.");
            return new ArrayList<>();
        }


    }
    /**
     * @param user
     * @return User
     * Save user, set active to false and send confirmation email.
     * Delete unconfirmed user.
     */
    public User saveUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setEmail(user.getEmail().toLowerCase());
        user.setActive(false);
        Role userRole = roleRepository.findByRole("ROLE_USER");
        user.setRoles(new HashSet<Role>(Collections.singletonList(userRole)));
        log.info("User saved: {}", user.getUserName());
        userRepository.save(user);

        ConfirmationToken confirmationToken = new ConfirmationToken();
        confirmationToken.setUser(user);
        confirmationTokenService.saveConfirmationToken(confirmationToken);
        String token = String.valueOf(confirmationToken.getToken());
        String topic = "Home BudgetApp - Confirm Registration";
        emailSender.send(user.getEmail(), getCreateConfirmationEmail(user.getUserName(), token), topic);

        Date fifteenMinutesInMilliseconds = new Date(System.currentTimeMillis() + 900000);
        scheduler.schedule(new DeleteUnconfirmedAccountTask(userRepository, confirmationTokenService, user,
                        confirmationToken),
        fifteenMinutesInMilliseconds);

        return user;
    }


    public User updateUser(User user) {
        user.setEmail(user.getEmail().toLowerCase());
        log.info("User updated: {}", user.getUserName());
        userRepository.save(user);
        return user;
    }

    public User updateUserPassword(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        log.info("Password for user: {} successfully updated", user.getUserName());
        return user;
    }

    /**
     * @param user
     * @return void
     * Method send delete confirmation token to user.
     */
    public void sendDeleteConfirmationToken(User user) {
        log.info("Deletion confirmation token sent to: {}", user.getUserName());

        ConfirmationToken confirmationToken = new ConfirmationToken();
        confirmationToken.setUser(user);
        confirmationTokenService.saveConfirmationToken(confirmationToken);
        String token = String.valueOf(confirmationToken.getToken());
        String topic = "Home BudgetApp - Are you sure you want to delete your account?";

        emailSender.send(user.getEmail(), getDeleteConfirmationEmail(user.getUserName(), token), topic);
    }

    public void deleteUser(User user) {

        userRepository.deleteUserById(user.getId());
        log.info("User: {} deleted successfully", user.getUserName());
    }

    /**
     * @param email
     * @param persistentUser
     * @return boolean
     * If user with email equals persistent user method will return true.
     * If user with email
     */
    public boolean emailCheck(String email, User persistentUser) {
        Optional<User> userToCheckEmail = userRepository.findUserByEmail(email);
        if (userToCheckEmail.isPresent() && persistentUser.equals(userToCheckEmail.orElseThrow(
                () -> {
                    log.error("entity not found with email: {} not found", email);
                    throw new EntityNotFoundException("entity not found");
                }
        )) || userToCheckEmail.isEmpty()) {
            log.info("User with email {} is the same user as the user with email: {} invoking update method or email is available.",
                    email, persistentUser.getEmail());
            return true;
        }
        log.info("Email: {} is not available.", email);
        return false;
    }

    public boolean usernameCheck(String userName, User persistentUser) {
        Optional<User> userToCheckUserName = userRepository.findUserByUserName(userName);
        if (userToCheckUserName.isPresent() && persistentUser.equals(userToCheckUserName.orElseThrow(
                () -> {
                    log.error("entity not found with username: {} not found", userName);
                    throw new EntityNotFoundException("entity not found");
                }
        )) || userToCheckUserName.isEmpty()) {
            log.info("User with username {} is the same user as the user with username: {} invoking update method or username is available.",
                    userName, persistentUser.getUserName());
            return true;
        }
        log.info("Username: {} is not available.", userName);
        return false;
    }

    public String getCreateConfirmationEmail(String userName, String token) {
        return String.format("<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <title>Email Confirmation</title>\n" +
                "    <p>Greetings %s! <br> In order to confirm registration of your account please click <a href=\"http://localhost:8080/api/user/confirm/registration/%s\">here</a>.</p>\n" +
                "<p>Link will only be valid for 15 minutes.</p>\n" +
                "<p>If you did not register your account please ignore this message.</p>\n" +
                "<p>Kind regards <br> Home BudgetApp Team</p>\n" +
                "</head>\n" +
                "<body>\n" +
                "\n" +
                "</body>\n" +
                "</html>", userName, token);
    }

    public String getDeleteConfirmationEmail(String userName, String token) {
        return String.format("<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <title>Email Confirmation</title>\n" +
                "    <p>Greetings %s! <br> Are you sure you want to delete your account? </p> \n" +
                "<p> If you really want to delete your account please click this <a href=\"http://localhost:8080/api/user/confirm/deletion/%s\">link</a>.</p>\n" +
                "<p>Remember that operation is irreversible and you will lose all content.</p>\n" +
                "<p>Link will only be valid for 15 minutes.</p>\n" +
                "<p>If you did not try to delete your account please ignore this message.</p>\n" +
                "<p>Kind regards <br> Home BudgetApp Team</p>\n" +
                "</head>\n" +
                "<body>\n" +
                "\n" +
                "</body>\n" +
                "</html>", userName, token);
    }




}
