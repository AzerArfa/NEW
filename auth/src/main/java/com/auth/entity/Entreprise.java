package com.auth.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "entreprises")
public class Entreprise {
	@Id
	@GeneratedValue(generator = "UUID")
    private UUID id;
    private String nom;
    private String adresse;
    private String secteuractivite;
    private String Matricule;
    private String ville;

    @ManyToMany(mappedBy = "entreprises", fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    private List<User> users = new ArrayList<>();
}
