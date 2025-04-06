package me.radek203.branchservice.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@ToString
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class BalanceChange {

    @Id
    private UUID id;
    private String account;
    private double amount;
    private BalanceChangeStatus status;
    private int branchId;

}
