package com.auth.services.auth;

import com.auth.dto.SignupRequest;
import com.auth.dto.UserDto;
import com.auth.entity.Role;
import com.auth.entity.User;
import com.auth.repository.RoleRepository;
import com.auth.repository.UserRepository;
import com.auth.dto.ChangePasswordDto;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Transactional
    public UserDto createUser(SignupRequest signupRequest) {
        User user = new User();
        user.setEmail(signupRequest.getEmail());
        user.setName(signupRequest.getName());
        user.setPassword(new BCryptPasswordEncoder().encode(signupRequest.getPassword()));
        Role userRole = roleRepository.findByName("USER");
        if (userRole == null) {
            userRole = new Role();
            userRole.setName("USER");
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
            Role userRole = roleRepository.findByName("USER");
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
        Role superAdminRole = roleRepository.findByName("SUPERADMIN");
        boolean hasSuperAdmin = superAdminRole != null && !superAdminRole.getUsers().isEmpty();

        if (!hasSuperAdmin) {
            User user = new User();
            user.setEmail("superadmin@test.com");
            user.setName("superadmin");
            user.setPassword(new BCryptPasswordEncoder().encode("superadmin"));
        
            if (superAdminRole == null) {
                superAdminRole = new Role();
                superAdminRole.setName("SUPERADMIN");
                roleRepository.save(superAdminRole);
            }
            user.getRoles().add(superAdminRole);
            userRepository.save(user);
        }
    }
}
