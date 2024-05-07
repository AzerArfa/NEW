package com.auth.services.auth;

import com.auth.dto.SignupRequest;
import com.auth.dto.UserDto;
import com.auth.entity.Entreprise;
import com.auth.entity.Role;
import com.auth.entity.User;
import com.auth.exceptions.UserNotFoundException;
import com.auth.repository.RoleRepository;
import com.auth.repository.UserRepository;
import com.auth.dto.ChangePasswordDto;
import com.auth.dto.EntrepriseDto;
import com.auth.dto.RoleDto;

import jakarta.annotation.PostConstruct;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    private EntrepriseService entrepriseService;
    
    public UserDto getUserByEmail(String email) throws UserNotFoundException {
        Optional<User> userOptional = userRepository.findFirstByEmail(email);
        if (userOptional.isPresent()) {
            return convertToUserDto(userOptional.get());
        } else {
            throw new UserNotFoundException("No user found with email: ");
        }
    }
    @PostConstruct
    public void testFindFirstByEmail() {
        Optional<User> user = userRepository.findFirstByEmail("image22@gmail.com");
        if (user.isPresent()) {
            System.out.println("User found: " + user.get().getEmail());
        } else {
            System.out.println("No user found with that email.");
        }
    }
    
    @Transactional
    public UserDto convertToUserDto(User user) {
        UserDto userDto = new UserDto();
        userDto.setId(userDto.getId());
        userDto.setName(userDto.getName());
        userDto.setEmail(userDto.getEmail());
        userDto.setImg(userDto.getImg());
        List<EntrepriseDto> entrepriseDtos = user.getEntreprises().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        userDto.setEntreprises(entrepriseDtos);
        userDto.setCreationDate(userDto.getCreationDate());
        List<RoleDto> roleDtos = userDto.getRoles().stream()
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
    public boolean deleteUserById(UUID userId) throws UserNotFoundException {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException("User not found with id: " + userId);
        }
        userRepository.deleteById(userId);
        return true;
    }

    @Transactional
    public UserDto createUser(SignupRequest signupRequest) {
    	  String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
    	    Pattern pattern = Pattern.compile(emailRegex);
    	    Matcher matcher = pattern.matcher(signupRequest.getEmail());
    	    
    	    if (!matcher.matches()) {
    	        throw new IllegalArgumentException("Invalid email format");
    	    }
        User user = new User();
        user.setEmail(signupRequest.getEmail());
        user.setName(signupRequest.getName());
        user.setImg(signupRequest.getImg());
        user.setCin(signupRequest.getCin());
        user.setDatenais(signupRequest.getDatenais());
        user.setLieunais(signupRequest.getLieunais());
        user.setPassword(new BCryptPasswordEncoder().encode(signupRequest.getPassword()));
        Role userRole = roleRepository.findByName("ROLE_USER");
        if (userRole == null) {
            userRole = new Role();
            userRole.setName("ROLE_USER");
            roleRepository.save(userRole);
        }
        user.getRoles().add(userRole);
        User createdUser = userRepository.save(user);
        return createdUser.getUserDto(); 
    }

    @Transactional
    public ResponseEntity<?> updatePasswordById(ChangePasswordDto changePasswordDto) {
        User user = null;
        try {
            Optional<User> userOptional = userRepository.findById(changePasswordDto.getId());
            if (userOptional.isPresent()) {
                user = userOptional.get();
                if (this.bCryptPasswordEncoder.matches(changePasswordDto.getOldPassword(), user.getPassword())) {
                    user.setPassword(bCryptPasswordEncoder.encode(changePasswordDto.getNewPassword()));
                    user.setCreationDate(new Date());
                    User updateUser = userRepository.save(user);
                    UserDto userDto = new UserDto();
                    userDto.setId(updateUser.getId());
                    return ResponseEntity.status(HttpStatus.OK).body(userDto);
                } else {
                    return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("Old password is incorrect");
                }
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error");
        }
    }
    @Transactional
    @Override
    public ResponseEntity<?> updateUserById(UserDto userDto) {
        try {
            Optional<User> userOptional = userRepository.findById(userDto.getId());
            if (!userOptional.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
            }

            User user = userOptional.get();
            user.setName(userDto.getName());
            user.setEmail(userDto.getEmail());
            user.setImg(userDto.getReturnedImg());
            user.setCin(userDto.getCin());
            user.setDatenais(userDto.getDatenais());
            user.setLieunais(userDto.getLieunais());
            // Assuming creationDate should not be updated:
            // user.setCreationDate(new Date());
            
            User updatedUser = userRepository.save(user);

            UserDto updatedUserDto = new UserDto();
            updatedUserDto.setId(updatedUser.getId());
            updatedUserDto.setName(updatedUser.getName());
            updatedUserDto.setEmail(updatedUser.getEmail());
            updatedUserDto.setReturnedImg(updatedUser.getImg());
            updatedUserDto.setCreationDate(updatedUser.getCreationDate());
            // Populate other fields as necessary

            return ResponseEntity.status(HttpStatus.OK).body(updatedUserDto);
        } catch (Exception e) {
            // Consider logging the exception here
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error");
        }
    }
    @Override
    public List<UserDto> searchUsersByName(String partialName) {
    	List<User> users =userRepository.findByNameContainingIgnoreCase(partialName);
        return users.stream().map(User::getUserDto).collect(Collectors.toList());
    }


    public Boolean hasUserWithEmail(String email) {
        return userRepository.findFirstByEmail(email).isPresent();
    }

    @Transactional
    public UserDto getUserById(UUID id) {
        Optional<User> optionalUser = userRepository.findById(id);
        return optionalUser.map(User::getUserDto).orElse(null);
    }

    @Transactional
    public List<UserDto> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream().map(User::getUserDto).collect(Collectors.toList());
    }
    @Transactional
    @Override
    public void addEntrepriseToUser(UUID userId, EntrepriseDto entrepriseDto) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        Entreprise entreprise = entrepriseService.createEntreprise(entrepriseDto);
        user.getEntreprises().add(entreprise); // assuming getter and setter are properly set up
        userRepository.save(user);
    }


    @Transactional
    public UserDto makeAdmin(UUID id) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            Role adminRole = roleRepository.findByName("ADMIN");
            user.getRoles().add(adminRole);
            return userRepository.save(user).getUserDto();
        }
        return null;
    }

    @Transactional
    public UserDto makeUser(UUID id) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            Role userRole = roleRepository.findByName("ROLE_USER");
            user.getRoles().add(userRole);
            return userRepository.save(user).getUserDto();
        }
        return null;
    }

    public boolean checkIfPasswordNeedsUpdate(User user) {
        Date lastCreationDate = user.getCreationDate();
        long differenceInMilliseconds = new Date().getTime() - lastCreationDate.getTime();
        long differenceInDays = differenceInMilliseconds / (1000 * 60 * 60 * 24);
        return differenceInDays >= 30;
    }

    @PostConstruct
    public void createAdminAccount() {
        Role superAdminRole = roleRepository.findByName("ROLE_SUPERADMIN");
        boolean hasSuperAdmin = superAdminRole != null && !superAdminRole.getUsers().isEmpty();

        if (!hasSuperAdmin) {
            User user = new User();
            user.setEmail("superadmin@test.com");
            user.setName("superadmin");
            user.setPassword(new BCryptPasswordEncoder().encode("superadmin"));
        
            if (superAdminRole == null) {
                superAdminRole = new Role();
                superAdminRole.setName("ROLE_SUPERADMIN");
                roleRepository.save(superAdminRole);
            }
            user.getRoles().add(superAdminRole);
            userRepository.save(user);
        }
    }
    @Transactional
    @Override
    public List<EntrepriseDto> getEntreprisesByUserId(UUID userId) {
        User user = userRepository.findById(userId).orElseThrow(
            () -> new RuntimeException("User not found"));
        Hibernate.initialize(user.getEntreprises());
        return user.getEntreprises().stream()
            .map(this::convertToDto)
            .collect(Collectors.toList());
    }


    private EntrepriseDto convertToDto(Entreprise entreprise) {
        EntrepriseDto dto = new EntrepriseDto();
        dto.setId(entreprise.getId());
        dto.setName(entreprise.getNom());
        dto.setAdresse(entreprise.getAdresse());
        dto.setSecteuractivite(entreprise.getSecteuractivite());
        dto.setMatricule(entreprise.getMatricule());
        dto.setCodeTVA(entreprise.getCodeTVA());
        dto.setSiegesociale(entreprise.getSiegesociale());
        dto.setVille(entreprise.getVille());
        return dto;
    }

    private UserDto convertUserToDto(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setName(user.getName());
        dto.setCin(user.getCin());
        dto.setDatenais(user.getDatenais());
        dto.setLieunais(user.getLieunais());
        dto.setRoles(user.getRoles().stream().map(this::convertRoleToDto).collect(Collectors.toList()));
        dto.setReturnedImg(user.getImg());
        dto.setCreationDate(user.getCreationDate());
        dto.setEntreprises(user.getEntreprises().stream().map(this::convertToDto).collect(Collectors.toList()));

        return dto;
    }

    private RoleDto convertRoleToDto(Role role) {
        RoleDto roleDto = new RoleDto();
        roleDto.setId(role.getId());
        roleDto.setName(role.getName());
        // Populate other necessary fields
        return roleDto;
    }


}
