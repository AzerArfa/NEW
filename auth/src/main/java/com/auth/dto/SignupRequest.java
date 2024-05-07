package com.auth.dto;

import java.util.Date;
import java.util.List;

import lombok.Data;

@Data
public class SignupRequest {

    private String email;
    private String password;
    private String name;
    private String societe;
    private Date datenais;
    private String lieunais;
    private String cin;
    private byte[] img;
}
