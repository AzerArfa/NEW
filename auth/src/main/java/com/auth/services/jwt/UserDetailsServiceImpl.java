package com.auth.services.jwt;

import com.auth.entity.Role;
import com.auth.entity.User;
import com.auth.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

	@Autowired
    private UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
	    Optional<User> optionalUser = userRepository.findFirstByEmail(username);
	    if (optionalUser.isEmpty()) {
	        throw new UsernameNotFoundException("Username not found");
	    }
	    User user = optionalUser.get();
	    List<SimpleGrantedAuthority> authorities = user.getRoles().stream()
	    	    .map(role -> new SimpleGrantedAuthority(role.getName())) // Assuming `getName()` method returns a string role name
	    	    .collect(Collectors.toList());

	    return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), authorities);
	}
}
