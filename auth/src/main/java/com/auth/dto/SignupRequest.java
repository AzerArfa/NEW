package com.auth.dto;

import java.util.List;

import lombok.Data;

@Data
public class SignupRequest {

    private String email;
    private String password;
    private String name;
    private String societe;
    private List<RoleDto> roles; 
    private byte[] img;
}
