package com.auth.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.auth.entity.Entreprise;

public interface EntrepriseRepository extends JpaRepository<Entreprise, UUID>{

}
