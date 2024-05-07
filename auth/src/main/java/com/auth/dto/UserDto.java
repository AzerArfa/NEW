package com.auth.dto;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

@Data
public class UserDto {

    private UUID id;
    private String email;
    private String name;
    private List<RoleDto> roles; 
    private MultipartFile img;
    private byte[] returnedImg;
    private Date creationDate;
    private List<EntrepriseDto> entreprises; 
    private Date datenais;
    private String lieunais;
    private String cin;
}
