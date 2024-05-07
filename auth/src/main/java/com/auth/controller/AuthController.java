package com.auth.controller;

import com.auth.dto.AuthenticationRequest;
import org.springframework.beans.factory.annotation.Autowired;
import com.auth.dto.ChangePasswordDto;
import com.auth.dto.EntrepriseDto;
import com.auth.dto.RoleDto;
import com.auth.dto.SignupRequest;
import com.auth.dto.UserDto;
import com.auth.exceptions.UserNotFoundException;
import com.auth.services.auth.AuthService;
import com.auth.services.auth.EntrepriseService;
import com.auth.services.auth.RoleService;
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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.access.prepost.PreAuthorize;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
	@Autowired
    private EntrepriseService entrepriseService;
	  private final AuthenticationManager authenticationManager;
	  @Autowired
	    private final AuthService authService;
	  @Autowired
	    private final UserDetailsServiceImpl userDetailsService; // Inject UserDetailsService
	  @Autowired
	    private final JwtUtil jwtUtil;
	  @Autowired
	    private RoleService roleService;
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


	    @PostMapping(value = "/signup", consumes = "multipart/form-data")
	    public ResponseEntity<?> signupUser(
	        @RequestParam("email") String email,
	        @RequestParam("name") String name,
	        @RequestParam("password") String password,
	        @RequestParam("cin") String cin,
	        @RequestParam("lieunais") String lieunais,
	        @RequestParam("datenais") Date datenais,
	        @RequestParam("img") MultipartFile img) {

	        try {
	            SignupRequest signupRequest = new SignupRequest();
	            signupRequest.setEmail(email);
	            signupRequest.setName(name);
	            signupRequest.setPassword(password);
	            signupRequest.setCin(cin);
	            signupRequest.setDatenais(datenais);
	            signupRequest.setLieunais(lieunais);
	            signupRequest.setImg(img.getBytes()); // Convert MultipartFile to byte[]

	            UserDto userDto = authService.createUser(signupRequest);
	            return ResponseEntity.ok(userDto);
	        } catch (IOException e) {
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing image");
	        }
	    }

   

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable UUID id) {
        try {
            boolean deleted = authService.deleteUserById(id);
            if (!deleted) {
                return ResponseEntity.badRequest().body("User could not be deleted.");
            }
            return ResponseEntity.ok().body("User deleted successfully.");
        } catch (UserNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
    @GetMapping("/roles")
    public ResponseEntity<List<RoleDto>> getAllRoles() {
        List<RoleDto> roles = roleService.getAllRoles();
        return ResponseEntity.ok(roles);
    }
    @GetMapping("/user/{id}")
    public ResponseEntity<?> getUserById(@PathVariable UUID id) {
        UserDto userDto = authService.getUserById(id);
        return userDto != null ? ResponseEntity.ok(userDto) : ResponseEntity.notFound().build();
    }

    @PostMapping("/make-admin/{id}")
    public ResponseEntity<?> makeAdmin(@PathVariable UUID id) {
        UserDto userDto = authService.makeAdmin(id);
        return userDto != null ? ResponseEntity.ok(userDto) : ResponseEntity.notFound().build();
    }
   
    @PostMapping("/make-user/{id}")
    public ResponseEntity<?> makeUser(@PathVariable UUID id) {
        UserDto userDto = authService.makeUser(id);
        return userDto != null ? ResponseEntity.ok(userDto) : ResponseEntity.notFound().build();
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserDto>> getAllUsers() {
        List<UserDto> users = authService.getAllUsers();
        return ResponseEntity.ok(users);
    }
    @GetMapping("/users/{name}")
    public ResponseEntity<List<UserDto>> getUsersByName(@PathVariable String name) {
        List<UserDto> users = authService.searchUsersByName(name);
        return ResponseEntity.ok(users);
    }
    @PutMapping("/updateuser/{id}")
    public ResponseEntity<?> updateUserById(@PathVariable UUID id, @RequestBody UserDto userDto) {
        if (!id.equals(userDto.getId())) {
            return ResponseEntity.badRequest().body("Mismatched ID in the path and the body");
        }
        return authService.updateUserById(userDto);
    }

    @PostMapping("/updatepassword")
    public ResponseEntity<?> updatePassword(@RequestBody ChangePasswordDto changePasswordDto) {
        try {
            return authService.updatePasswordById(changePasswordDto);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Something went wrong");
        }
    }
    @PostMapping("/user/{userId}/add-entreprise")
    public ResponseEntity<?> addEntrepriseToUser(@PathVariable UUID userId, @RequestBody EntrepriseDto entrepriseDto) {
        try {
        	authService.addEntrepriseToUser(userId, entrepriseDto); // assuming this is the name of the method in UserService
            return ResponseEntity.ok("Entreprise added successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error adding entreprise: " + e.getMessage());
        }
    }
    @GetMapping("/entreprises")
    public ResponseEntity<List<EntrepriseDto>> getAllEntreprises() {
        List<EntrepriseDto> entreprises = entrepriseService.getAllEntreprises();
        return ResponseEntity.ok(entreprises);
    }
    @GetMapping("/user/{userId}/entreprises")
    public ResponseEntity<?> getEntreprisesByUserId(@PathVariable UUID userId) {
        try {
            List<EntrepriseDto> entreprises = authService.getEntreprisesByUserId(userId);
            return ResponseEntity.ok(entreprises);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }


}
