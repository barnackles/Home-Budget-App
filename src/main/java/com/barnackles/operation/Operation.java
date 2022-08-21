package com.barnackles.operation;

import com.barnackles.category.Category;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "operations")
public class Operation {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "operation_number", unique = true)
    private UUID uuid;
    @Length(max = 200)
    private String description;
    @ManyToOne
    @JoinColumn(name = "category_id")
    @NotBlank
    private Category category;
    @DecimalMin(value = "0.01")
    private BigDecimal amount;
    @NotBlank
    private OperationType operationType;
    @NotBlank
    private LocalDateTime operationDateTime;
    @NotBlank
    private ActualOrPlanned actualOrPlanned;
    @NotBlank
    private OperationFrequency frequency;


    @PrePersist
    public void prePersist() {
        //check for collisions
        uuid = UUID.randomUUID();
    }


}
