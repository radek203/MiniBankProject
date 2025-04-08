package me.radek203.headquarterservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

@Getter
@Setter
@ToString
@Entity(name = "client")
public class Client {

    @Id
    private UUID id;
    private ClientStatus status;
    private String firstName;
    private String lastName;
    private int userId;
    private String phone;
    private String address;
    private String city;

    private int branch;
    @Column(unique = true)
    private String accountNumber;
    private double balance;
    private double balanceReserved;

}
