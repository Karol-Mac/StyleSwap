package com.restapi.styleswap.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String stripeAccountId;

    private boolean stripeAccountCreated = false;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    private String firstName;
    private String lastName;

    @Column(length = 9)
    private String phoneNumber;


    @Column(nullable = false)
    private String password;

    //relations:
    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinTable(name = "users_roles",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id")
    )
    private Set<Role> roles;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Clothe> myClothes;

    @OneToMany(mappedBy = "buyer", cascade = CascadeType.ALL)
    private Set<Conversation> conversations;

    @OneToMany(mappedBy = "buyer")
    private Set<Order> orders;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private Storage storage;

    @OneToMany(mappedBy = "seller")
    private Set<Order> soldOrders;

    public User(String email) {
        this.email = email;
    }
}