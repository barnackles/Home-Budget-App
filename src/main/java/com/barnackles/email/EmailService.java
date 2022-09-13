package com.barnackles.email;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailService implements EmailSender {

    private final JavaMailSender mailSender;

    @Override
    @Async // use queue
    public void send(String to, String email, String topic) {

        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, "utf-8");
            mimeMessageHelper.setText(email, true);
            mimeMessageHelper.setTo(to);
            mimeMessageHelper.setSubject(topic);
            mimeMessageHelper.setFrom("homebudgetapp@gmx.com");
            log.info("Confirmation email sent to: {}", to);
            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            log.error(e.getMessage());
            throw  new IllegalStateException("failed to send email");
        }

    }


}
