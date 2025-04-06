package me.radek203.branchservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@ToString
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Transfer {

    @Id
    private UUID id;
    private String fromAccount;
    private String toAccount;
    private double amount;
    private String message;
    private TransferStatus status;
    //@Transient
    private int fromBranchId;
    //@Transient
    private int toBranchId;
    private long date;

}
