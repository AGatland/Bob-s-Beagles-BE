package com.booleanuk.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank
    @Size(max = 50)
    @Email
    private String email;

    private String username;

    @NotBlank
    @Size(max = 120)
    private String password;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "phone_number", nullable = false)
    private String phone;

    @Column(name = "address", nullable = false)
    private String address;

    @Column(name = "consent_spam", nullable = false)
    private boolean consentSpam;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

    @ElementCollection
    @CollectionTable(name = "basket",
            joinColumns = @JoinColumn(name = "user_id"))
    @MapKeyJoinColumn(name = "product_id")
    @Column(name = "quantity")
    @JsonIgnore
    private Map<Product, Integer> basket = new HashMap<>();

    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private List<Order> orders;

    public User(String email, String firstName, String lastName, String password, String phone, String address, boolean consentSpam) {
        this.email = email;
        this.username = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
        this.phone = phone;
        this.address = address;
        this.consentSpam = consentSpam;
    }
}
