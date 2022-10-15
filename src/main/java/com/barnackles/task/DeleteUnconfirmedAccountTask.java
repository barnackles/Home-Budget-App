package com.barnackles.task;

import com.barnackles.confirmationToken.ConfirmationToken;
import com.barnackles.confirmationToken.ConfirmationTokenService;
import com.barnackles.user.User;
import com.barnackles.user.UserRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Slf4j
@Transactional
public class DeleteUnconfirmedAccountTask implements Runnable {

    private final UserRepository userRepository;
    private final ConfirmationTokenService confirmationTokenService;
    private Long userId;

    private ConfirmationToken confirmationToken;


    @Override
    public void run() {

        User userToVerify = userRepository.findUserById(userId).orElseThrow(() -> {
            log.error("entity with id: {} not found", userId);
            throw new EntityNotFoundException("entity not found");
        });

        if (LocalDateTime.now().isAfter(confirmationToken.getExpirationTime()) && (!userToVerify.getActive())) {
            log.info("Unconfirmed user: {} deleted on: {}, with expiration time: {}", userToVerify.getUserName(), LocalDateTime.now(),
                    confirmationToken.getExpirationTime());
            confirmationTokenService.deleteConfirmationToken(confirmationToken);
            userRepository.deleteUserById(userToVerify.getId());
        }

    }

}

