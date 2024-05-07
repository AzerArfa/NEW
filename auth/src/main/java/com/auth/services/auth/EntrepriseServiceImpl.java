package com.auth.services.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.auth.dto.EntrepriseDto;
import com.auth.dto.UserDto;
import com.auth.entity.Entreprise;
import com.auth.entity.User;
import com.auth.repository.EntrepriseRepository;
import com.auth.repository.UserRepository;

import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class EntrepriseServiceImpl implements EntrepriseService {

    @Autowired
    private EntrepriseRepository entrepriseRepository;
    @Autowired
    private UserRepository userRepository;

    @Transactional
    @Override
    public Entreprise createEntreprise(EntrepriseDto entrepriseDto) {
        Entreprise entreprise = new Entreprise(); // conversion logic here
        entreprise.setNom(entrepriseDto.getName());
        entreprise.setAdresse(entrepriseDto.getAdresse());
        entreprise.setSecteuractivite(entrepriseDto.getSecteuractivite());
        entreprise.setLogo(entrepriseDto.getReturnedImg());
        entreprise.setMatricule(entrepriseDto.getMatricule());
        entreprise.setVille(entrepriseDto.getVille());
        entreprise.setCodeTVA(entrepriseDto.getCodeTVA());
        entreprise.setSiegesociale(entrepriseDto.getSiegesociale());
        return entrepriseRepository.save(entreprise);
    }


//    @Override
//    public EntrepriseDto updateEntreprise(UUID id, EntrepriseDto entrepriseDto) {
//        Entreprise entreprise = entrepriseRepository.findById(id)
//            .orElseThrow(() -> new RuntimeException("Entreprise not found"));
//        updateEntrepriseFromDto(entreprise, entrepriseDto);
//        entreprise = entrepriseRepository.save(entreprise);
//        return convertToDto(entreprise);
//    }

    @Override
    public void deleteEntreprise(UUID id) {
        entrepriseRepository.deleteById(id);
    }

    @Override
    public EntrepriseDto getEntrepriseById(UUID id) {
        Entreprise entreprise = entrepriseRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Entreprise not found"));
        return convertToDto(entreprise);
    }

    @Override
    public List<EntrepriseDto> getAllEntreprises() {
        return entrepriseRepository.findAll().stream()
            .map(this::convertToDto)
            .collect(Collectors.toList());
    }

    private EntrepriseDto convertToDto(Entreprise entreprise) {
        EntrepriseDto dto = new EntrepriseDto();
        dto.setId(entreprise.getId());
        dto.setName(entreprise.getNom());
        dto.setAdresse(entreprise.getAdresse());
        dto.setReturnedImg(entreprise.getLogo());
        dto.setSecteuractivite(entreprise.getSecteuractivite());
        dto.setMatricule(entreprise.getMatricule());
        dto.setVille(entreprise.getVille());
        dto.setCodeTVA(entreprise.getCodeTVA());
        dto.setSiegesociale(entreprise.getSiegesociale());
        return dto;
    }

    private void updateEntrepriseFromDto(Entreprise entreprise, EntrepriseDto dto) {
        entreprise.setNom(dto.getName());
        entreprise.setAdresse(dto.getAdresse());
        entreprise.setSecteuractivite(dto.getSecteuractivite());
        entreprise.setMatricule(dto.getMatricule());
        entreprise.setVille(dto.getVille());
        // Assuming you have a way to handle user updates or it is handled elsewhere
    }

    private UserDto convertUserToDto(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setName(user.getName());
        // Add other fields if necessary
        return dto;
    }
 
}
