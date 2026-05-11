package com.hospital.app.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "doctors")
@Getter @Setter @NoArgsConstructor
public class Doctor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "First name is required")
    @Size(max = 50)
    @Pattern(regexp = "^[a-zA-ZΑ-Ωα-ωάέήίόύώΆΈΉΊΌΎΏ ]+$", message = "First name must contain only letters")
    @Column(nullable = false, length = 50)
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(max = 50)
    @Pattern(regexp = "^[a-zA-ZΑ-Ωα-ωάέήίόύώΆΈΉΊΌΎΏ ]+$", message = "Last name must contain only letters")
    @Column(nullable = false, length = 50)
    private String lastName;

    @NotBlank(message = "Specialty is required")
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String specialty;

    @NotBlank(message = "License number is required")
    @Pattern(regexp = "^[A-Z0-9]{5,15}$", message = "Invalid license number format")
    @Column(nullable = false, unique = true, length = 15)
    private String licenseNumber;

    @NotBlank(message = "Phone is required")
    @Pattern(regexp = "^[0-9+\\-() ]{7,20}$", message = "Invalid phone number")
    @Column(length = 20)
    private String phone;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "doctor", cascade = CascadeType.ALL)
    private List<Visit> visits;
}
