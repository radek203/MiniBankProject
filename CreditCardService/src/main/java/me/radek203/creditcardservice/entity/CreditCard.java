package me.radek203.creditcardservice.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class CreditCard {

    @Id
    private String cardNumber;
    private String accountNumber;
    private String expirationDate;
    private String cvv;

    @ManyToOne
    private Bank bank;

}
