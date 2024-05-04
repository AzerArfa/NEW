package com.auth.controller;

import com.auth.dto.AuthenticationRequest;
import com.auth.dto.ChangePasswordDto;
import com.auth.dto.SignupRequest;
import com.auth.dto.UserDto;
import com.auth.services.auth.AuthService;
import com.auth.services.jwt.UserDetailsServiceImpl;
import com.auth.utils.JwtUtil;
import org.springframework.security.core.userdetails.UserDetails; // Import UserDetails
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;
import java.util.UUID;
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

	  private final AuthenticationManager authenticationManager;
	    private final AuthService authService;
	    private final UserDetailsServiceImpl userDetailsService; // Inject UserDetailsService
	    private final JwtUtil jwtUtil;

	    @PostMapping("/login")
	    public void createAuthenticationToken(@RequestBody AuthenticationRequest authenticationRequest,
	                                          HttpServletResponse response) throws Exception {
	    	System.out.println(authenticationRequest);
	        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
	                authenticationRequest.getEmail(), authenticationRequest.getPassword()));

	        final UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getEmail());
	        final String jwt = jwtUtil.generateToken(userDetails);

	        response.setHeader("Access-Control-Allow-Origin", "*");
	        response.setHeader("Access-Control-Allow-Methods", "POST");
	        response.setHeader("Access-Control-Allow-Headers", "Authorization, Content-Type");
	        response.addHeader("Authorization", "Bearer " + jwt);
	    }


    @PostMapping("/signup")
    public ResponseEntity<?> signupUser(@RequestBody SignupRequest signupRequest) {
        if (authService.hasUserWithEmail(signupRequest.getEmail())) {
            return new ResponseEntity<>("User already exists", HttpStatus.NOT_ACCEPTABLE);
        }

        UserDto userDto = authService.createUser(signupRequest);
        return new ResponseEntity<>(userDto, HttpStatus.OK);
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<?> getUserById(@PathVariable UUID id) {
        UserDto userDto = authService.getUserById(id);
        return userDto != null ? ResponseEntity.ok(userDto) : ResponseEntity.notFound().build();
    }

    @GetMapping("/make-admin/{id}")
    public ResponseEntity<?> makeAdmin(@PathVariable UUID id) {
        UserDto userDto = authService.makeAdmin(id);
        return userDto != null ? ResponseEntity.ok(userDto) : ResponseEntity.notFound().build();
    }
   
    @GetMapping("/make-user/{id}")
    public ResponseEntity<?> makeUser(@PathVariable UUID id) {
        UserDto userDto = authService.makeUser(id);
        return userDto != null ? ResponseEntity.ok(userDto) : ResponseEntity.notFound().build();
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserDto>> getAllUsers() {
        List<UserDto> users = authService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @PostMapping("/updatepassword")
    public ResponseEntity<?> updatePassword(@RequestBody ChangePasswordDto changePasswordDto) {
        try {
            return authService.updatePasswordById(changePasswordDto);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Something went wrong");
        }
    }
}
