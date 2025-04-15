package me.radek203.headquarterservice.entity;

import jakarta.persistence.Id;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Transfer {

    @Id
    private UUID id;
    private String fromAccount;
    private String toAccount;
    private double amount;
    private TransferStatus status;
    private int fromBranchId;
    private int toBranchId;
    private long date;

}
