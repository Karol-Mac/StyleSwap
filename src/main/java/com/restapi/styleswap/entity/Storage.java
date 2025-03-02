package com.restapi.styleswap.entity;


import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Set;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "storages")
public class Storage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    @EqualsAndHashCode.Exclude
    private User user;

    @ManyToMany
    @JoinTable(
            name = "storages_clothes",
            joinColumns = @JoinColumn(name = "storage_id"),
            inverseJoinColumns = @JoinColumn(name = "clothe_id")
    )
    @EqualsAndHashCode.Exclude
    private Set<Clothe> clothes;
}
