package com.barnackles.confirmationToken;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ConfirmationTokenService {

    private final ConfirmationTokenRepository confirmationTokenRepository;

    public void saveConfirmationToken(ConfirmationToken confirmationToken) {
        log.info("Confirmation token saved: {}", confirmationToken.getToken());
        confirmationTokenRepository.save(confirmationToken);
    }

    public void updateConfirmationToken(ConfirmationToken confirmationToken) {
        log.info("Token updated");
        confirmationTokenRepository.save(confirmationToken);
    }

    public ConfirmationToken findConfirmationTokenByToken(UUID token) {
        return confirmationTokenRepository.findConfirmationTokenByToken(token).orElseThrow(
                () -> {
                    log.error("Confirmation token : {} not found", token);
                    throw new EntityNotFoundException("entity not found");
                }
        );
    }

}
