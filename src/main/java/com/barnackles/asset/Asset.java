package com.barnackles.asset;

import com.barnackles.operation.Operation;
import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Data
@Table(name = "assets")

public class Asset {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int Id;
    private String name;
    private String description;
    private BigDecimal value;
    @OneToMany
    @JoinColumn(name = "operation_id")
    private List<Operation> operations;

}
