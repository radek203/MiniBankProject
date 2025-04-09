package me.radek203.branchservice.entity;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CreditCard {

    @Id
    private String cardNumber;
    private String accountNumber;
    private String expirationDate;
    private String cvv;

}
