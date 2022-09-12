package com.barnackles.task;

import com.barnackles.confirmationToken.ConfirmationToken;
import com.barnackles.confirmationToken.ConfirmationTokenService;
import com.barnackles.user.User;
import com.barnackles.user.UserRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import javax.transaction.Transactional;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Slf4j
@Transactional
public class DeleteUnconfirmedAccountTask implements Runnable {

    private final UserRepository userRepository;
    private final ConfirmationTokenService confirmationTokenService;
    private User user;

    private ConfirmationToken confirmationToken;


    @Override
    public void run() {

        if (LocalDateTime.now().isAfter(confirmationToken.getExpirationTime()) && !user.getActive()) {
            log.info("Unconfirmed user: {} deleted on: {}, with expiration time: {}", user.getUserName(), LocalDateTime.now(),
                    confirmationToken.getExpirationTime());
            confirmationTokenService.deleteConfirmationToken(confirmationToken);
            userRepository.deleteUserById(user.getId());
        }
    }

}

