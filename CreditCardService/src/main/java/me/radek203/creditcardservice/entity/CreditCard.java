package me.radek203.creditcardservice.entity;

import jakarta.persistence.*;
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

    @ManyToOne(cascade = CascadeType.ALL)
    private Bank bank;

}
