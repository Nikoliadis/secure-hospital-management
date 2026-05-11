package com.hospital.app.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "patients")
@Getter @Setter @NoArgsConstructor
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "First name is required")
    @Size(max = 50, message = "First name must be at most 50 characters")
    @Pattern(regexp = "^[a-zA-ZΑ-Ωα-ωάέήίόύώΆΈΉΊΌΎΏ ]+$", message = "First name must contain only letters")
    @Column(nullable = false, length = 50)
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(max = 50, message = "Last name must be at most 50 characters")
    @Pattern(regexp = "^[a-zA-ZΑ-Ωα-ωάέήίόύώΆΈΉΊΌΎΏ ]+$", message = "Last name must contain only letters")
    @Column(nullable = false, length = 50)
    private String lastName;

    @NotNull(message = "Date of birth is required")
    @Past(message = "Date of birth must be in the past")
    @Column(nullable = false)
    private LocalDate dateOfBirth;

    @NotBlank(message = "AMKA is required")
    @Pattern(regexp = "^\\d{11}$", message = "AMKA must be exactly 11 digits")
    @Column(nullable = false, unique = true, length = 11)
    private String amka;

    @NotBlank(message = "Phone is required")
    @Pattern(regexp = "^[0-9+\\-() ]{7,20}$", message = "Invalid phone number")
    @Column(length = 20)
    private String phone;

    @Email(message = "Invalid email format")
    @Column(length = 100)
    private String email;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL)
    private List<Visit> visits;
}
