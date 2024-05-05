package com.auth.dto;

import java.util.UUID;

import lombok.Data;

@Data
public class ChangePasswordDto {

    private UUID id;

    private String oldPassword;

    private String newPassword;

}
