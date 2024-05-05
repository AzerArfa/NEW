package com.auth.dto;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import lombok.Data;

@Data
public class UserDto {

    private UUID id;
    private String email;
    private String name;
    private List<RoleDto> roles; 
    private byte[] img;
    private Date creationDate;
    private String societe; 
}
