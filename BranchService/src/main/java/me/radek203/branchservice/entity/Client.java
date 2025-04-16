package me.radek203.branchservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
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
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    private ClientStatus status;
    @NotNull
    @Size(min = 2, max = 50, message = "error/client-first-name-size")
    private String firstName;
    @NotNull
    @Size(min = 2, max = 50, message = "error/client-last-name-size")
    private String lastName;
    private int userId;
    @NotNull
    @Pattern(regexp = "^\\+?\\d{2,3} \\d{9,15}$", message = "error/client-phone-number")
    private String phone;
    @NotNull
    @Size(min = 2, max = 50, message = "error/client-address-size")
    private String address;
    @NotNull
    @Size(min = 2, max = 50, message = "error/client-city-size")
    private String city;

    private int branch;
    @Column(unique = true)
    private String accountNumber;
    private double balance;
    private double balanceReserved;

}
