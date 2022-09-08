package com.barnackles.confirmationToken;

import com.barnackles.user.User;
import com.fasterxml.uuid.Generators;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
public class ConfirmationToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true, columnDefinition = "BINARY(16)", nullable = false)
    private UUID token;
    @Column(nullable = false)
    private LocalDateTime creationTime;
    @Column(nullable = false)
    private LocalDateTime expirationTime;
    private LocalDateTime confirmationTime;
    @ManyToOne
    @JoinColumn(nullable = false)
    private User user;

    @PrePersist
    private void prePersist() {
        //check for collisions
        token = Generators.timeBasedGenerator().generate();
        creationTime = LocalDateTime.now();
        expirationTime = creationTime.plusMinutes(15L);
    }



}
