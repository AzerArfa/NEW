package com.auth.services.auth;

import java.util.List;
import java.util.UUID;

import com.auth.dto.EntrepriseDto;
import com.auth.entity.Entreprise;

public interface EntrepriseService {
	Entreprise createEntreprise(EntrepriseDto entrepriseDto);
//    EntrepriseDto updateEntreprise(UUID id, EntrepriseDto entrepriseDto);
    void deleteEntreprise(UUID id);
    EntrepriseDto getEntrepriseById(UUID id);
    List<EntrepriseDto> getAllEntreprises();
}
