package com.auth.dto;

import java.util.Set;
import java.util.UUID;

import lombok.Data;
@Data
public class EntrepriseDto {
	 private UUID id;
	    private String name;
	    private String adresse;
	    private String secteuractivite;
	    private String Matricule;
	    private String ville;
	    private Set<UserDto> users;
}
