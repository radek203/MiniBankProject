package me.radek203.creditcardservice.entity;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Transfer {

    private UUID id;
    private String fromAccount;
    private String toAccount;
    private double amount;
    private String status;
    private int fromBranchId;
    private int toBranchId;
    private long date;

}
