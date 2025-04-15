package me.radek203.headquarterservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Transient;
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

    @Transient
    private ClientStatus status;
    @Transient
    private String firstName;
    @Transient
    private String lastName;
    private int userId;
    @Transient
    private String phone;
    @Transient
    private String address;
    @Transient
    private String city;

    private int branch;
    @Column(unique = true)
    private String accountNumber;
    private double balance;

    @Transient
    private double balanceReserved;

}
