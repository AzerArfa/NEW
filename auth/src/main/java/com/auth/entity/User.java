package com.auth.entity;

import java.util.*;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;

import com.auth.dto.EntrepriseDto;
import com.auth.dto.RoleDto;
import com.auth.dto.UserDto;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "users")
public class User implements UserDetails {

	@Id
    @GeneratedValue(generator = "UUID")
    private UUID id;

    private String email;
    private String cin;
    private Date datenais;
    private String lieunais;
    private String password;

    private String name;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private List<Role> roles = new ArrayList<>();
    
    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    @JoinTable(
            name = "user_entreprises",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "entreprise_id")
    )
    private List<Entreprise> entreprises = new ArrayList<>();
    
    


    @Temporal(TemporalType.TIMESTAMP)
    private Date creationDate = new Date();
    
    @Lob
    @Column(columnDefinition = "longblob")
    private byte[] img;

    @Transactional
    public UserDto getUserDto() {
        UserDto userDto = new UserDto();
        userDto.setId(id);
        userDto.setName(name);
        userDto.setEmail(email);
        userDto.setReturnedImg(img);
        userDto.setDatenais(datenais);
        userDto.setLieunais(lieunais);
        userDto.setCin(cin);
        List<EntrepriseDto> entrepriseDtos = entreprises.stream()
                .map(entreprise -> {
                    EntrepriseDto entrepriseDto = new EntrepriseDto();
                    entrepriseDto.setId(entreprise.getId());
                    entrepriseDto.setName(entreprise.getNom());
                    entrepriseDto.setAdresse(entreprise.getAdresse());
                    entrepriseDto.setSecteuractivite(entreprise.getSecteuractivite());
                    entrepriseDto.setMatricule(entreprise.getMatricule());
                    entrepriseDto.setVille(entreprise.getVille());
                    return entrepriseDto;
                })
                .collect(Collectors.toList());
        userDto.setEntreprises(entrepriseDtos);

        userDto.setCreationDate(creationDate);
        List<RoleDto> roleDtos = roles.stream()
                .map(role -> {
                    RoleDto roleDto = new RoleDto();
                    roleDto.setId(role.getId());
                    roleDto.setName(role.getName());
                    return roleDto;
                })
                .collect(Collectors.toList());
userDto.setRoles(roleDtos);
        return userDto;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getUsername() {
        return null;
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }
}