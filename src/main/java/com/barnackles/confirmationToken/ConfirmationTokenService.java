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
        String token = String.valueOf(confirmationToken.getToken());
        log.info("Confirmation token saved.");
        confirmationTokenRepository.save(confirmationToken);
    }

    public void updateConfirmationToken(ConfirmationToken confirmationToken) {
        confirmationTokenRepository.save(confirmationToken);
        log.info("Token updated");
    }

    public void deleteConfirmationToken(ConfirmationToken confirmationToken) {
        confirmationTokenRepository.delete(confirmationToken);
        log.info("Token deleted");
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
