package com.barnackles.confirmationToken;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConfirmationTokenService {

    private final ConfirmationTokenRepository confirmationTokenRepository;

    public void saveConfirmationToken(ConfirmationToken confirmationToken) {
        log.info("Confirmation token saved: {}", confirmationToken.toString());
        confirmationTokenRepository.save(confirmationToken);
    }

}
