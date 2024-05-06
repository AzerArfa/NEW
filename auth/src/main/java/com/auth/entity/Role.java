package com.auth.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;
import java.util.UUID;
import java.util.ArrayList;

@Entity
@Data
@Table(name = "roles")
public class Role {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    @ManyToMany(mappedBy = "roles", fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    private List<User> users = new ArrayList<>();

  
}