package com.auth.services.auth;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;

import com.auth.dto.ChangePasswordDto;
import com.auth.dto.SignupRequest;
import com.auth.dto.UserDto;
import com.auth.entity.User;
import com.auth.exceptions.UserNotFoundException;

import org.springframework.stereotype.Service;
@Service
public interface AuthService {

    UserDto createUser(SignupRequest signupRequest);

    Boolean hasUserWithEmail(String email);

    UserDto getUserById(UUID id);
    List<UserDto> getAllUsers();
    public boolean checkIfPasswordNeedsUpdate(User user);
    UserDto makeAdmin(UUID id);

	ResponseEntity<?> updatePasswordById(ChangePasswordDto changePasswordDto);

	UserDto makeUser(UUID id);
	boolean deleteUserById(UUID userId) throws UserNotFoundException;

}
