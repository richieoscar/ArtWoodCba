package com.richieoscar.artwoodcba.domain;

import com.richieoscar.artwoodcba.dto.enums.Status;
import com.richieoscar.artwoodcba.dto.enums.SystemRole;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Entity
@Setter
@ToString
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(unique = true, nullable = false)
    private String email;

    @Column(unique = true, nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    private SystemRole role;

    @Column(unique = true, nullable = false)
    private String phone;

    @Column
    private String firstName;

    @Column
    private String lastName;

    @Enumerated(EnumType.STRING)
    private Status status;

    private LocalDateTime registrationDate;

    private LocalDateTime lastLoginDate;

    @Column(unique = true, nullable = false)
    private String customerId;

}
