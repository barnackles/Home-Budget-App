package com.barnackles.confirmationToken;

import com.barnackles.user.User;
import com.fasterxml.uuid.Generators;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@EqualsAndHashCode
@Table(name = "confirmation_tokens")
public class ConfirmationToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true, columnDefinition = "BINARY(16)", nullable = false)
    @EqualsAndHashCode.Include()
    private UUID token;
    @Column(nullable = false)
    private LocalDateTime creationTime;
    @Column(nullable = false)
    private LocalDateTime expirationTime;
    private LocalDateTime confirmationTime;
    @ManyToOne
    @JoinColumn(name = "user_token")
    private User user;

    @PrePersist
    private void prePersist() {
        //check for collisions
        token = Generators.timeBasedGenerator().generate();
        creationTime = LocalDateTime.now();
        expirationTime = creationTime.plusMinutes(15L);
    }



}
