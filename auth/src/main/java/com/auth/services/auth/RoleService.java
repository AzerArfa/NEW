package com.auth.services.auth;

import com.auth.dto.RoleDto;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
@Service
public interface RoleService {
    RoleDto createRole(RoleDto roleDto);
    List<RoleDto> getAllRoles();
    RoleDto getRoleById(UUID id);
    RoleDto updateRole(UUID id, RoleDto roleDto);
    void deleteRole(UUID id);
}
