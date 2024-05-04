package com.auth.services.auth;
import com.auth.entity.Role;
import com.auth.repository.RoleRepository;
import com.auth.services.auth.RoleService;
import com.auth.dto.RoleDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleRepository roleRepository;
    private static final Logger log = LoggerFactory.getLogger(RoleServiceImpl.class);
    @Override
    public RoleDto createRole(RoleDto roleDto) {
        Role role = new Role();
        role.setName(roleDto.getName());
        role = roleRepository.save(role);
        return convertToDto(role);
    }
    @Transactional
    @Override
    public List<RoleDto> getAllRoles() {
        return roleRepository.findAll().stream().map(this::convertToDto).collect(Collectors.toList());
    }

    @Override
    public RoleDto getRoleById(UUID id) {
        Role role = roleRepository.findById(id).orElse(null);
        return role != null ? convertToDto(role) : null;
    }

    @Override
    public RoleDto updateRole(UUID id, RoleDto roleDto) {
        Role role = roleRepository.findById(id).orElse(null);
        if (role != null) {
            role.setName(roleDto.getName());
            role = roleRepository.save(role);
            return convertToDto(role);
        }
        return null;
    }

    @Override
    public void deleteRole(UUID id) {
        roleRepository.deleteById(id);
    }

    private RoleDto convertToDto(Role role) {
        RoleDto roleDto = new RoleDto();
        roleDto.setId(role.getId());
        roleDto.setName(role.getName());
        return roleDto;
    }

    @PostConstruct
    @Transactional
    public void initRoles() {
        try {
            ensureRoleExists("SUPERADMIN");
            ensureRoleExists("ADMIN");
            ensureRoleExists("CLIENT");
        } catch (Exception e) {
            log.error("Failed to initialize roles", e);
        }
    }

    private void ensureRoleExists(String roleName) {
        Role role = roleRepository.findByName(roleName);
        if (role == null) {
            role = new Role();
            role.setName(roleName);
            roleRepository.save(role);
        }
    }
}