package me.radek203.headquarterservice.entity;

import jakarta.persistence.Id;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class BalanceChange {

    @Id
    private UUID id;
    private String account;
    private double amount;
    private BalanceChangeStatus status;
    private int branchId;
    private long date;

}
