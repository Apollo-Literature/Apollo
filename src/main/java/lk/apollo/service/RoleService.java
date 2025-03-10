package lk.apollo.service;

import lk.apollo.dto.RoleDTO;
import lk.apollo.exception.ResourceAlreadyExistsException;
import lk.apollo.exception.ResourceNotFoundException;
import lk.apollo.mapper.RoleMapper;
import lk.apollo.model.Permission;
import lk.apollo.model.Role;
import lk.apollo.repository.PermissionRepository;
import lk.apollo.repository.RoleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * The type Role service.
 */
@Service
public class RoleService {
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final RoleMapper roleMapper;

    /**
     * Instantiates a new Role service.
     *
     * @param roleRepository       the role repository
     * @param permissionRepository the permission repository
     * @param roleMapper           the role mapper
     */
    public RoleService(RoleRepository roleRepository, PermissionRepository permissionRepository, RoleMapper roleMapper) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
        this.roleMapper = roleMapper;
    }


    /**
     * Gets all roles.
     *
     * @return the all roles
     */
    @Transactional(readOnly = true)
    public List<RoleDTO> getAllRoles() {
        List<Role> roles = roleRepository.findAll();
        return roleMapper.toDtoList(roles);
    }

    @Transactional(readOnly = true)
    public RoleDTO getRoleById(Long id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with id: " + id));
        return roleMapper.toDto(role);
    }

    /**
     * Gets role by name.
     *
     * @param name the name
     * @return the role by name
     */
    @Transactional(readOnly = true)
    public RoleDTO getRoleByName(String name) {
        Role role = roleRepository.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with name: " + name));
        return roleMapper.toDto(role);
    }

    /**
     * Create role role dto.
     *
     * @param roleDTO the role dto
     * @return the role dto
     */
    @Transactional
    public RoleDTO createRole(RoleDTO roleDTO) {
        if (roleRepository.existsByName(roleDTO.getName())) {
            throw new ResourceAlreadyExistsException("Role already exists with name: " + roleDTO.getName());
        }

        Role role = roleMapper.toEntity(roleDTO);
        role = roleRepository.save(role);
        return roleMapper.toDto(role);
    }

    /**
     * Update role role dto.
     *
     * @param roleDTO the role dto
     * @return the role dto
     */
    @Transactional
    public RoleDTO updateRole(RoleDTO roleDTO) {
        Role existingRole = roleRepository.findById(roleDTO.getRoleId())
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with id: " + roleDTO.getRoleId()));

        if (!existingRole.getName().equals(roleDTO.getName()) && roleRepository.existsByName(roleDTO.getName())) {
            throw new ResourceAlreadyExistsException("Role already exists with name: " + roleDTO.getName());
        }

        roleMapper.updateRoleFromDto(roleDTO, existingRole);
        Role savedRole = roleRepository.save(existingRole);
        return roleMapper.toDto(savedRole);
    }

    /**
     * Delete role.
     *
     * @param id the id
     */
    @Transactional
    public void deleteRole(Long id) {
        if (!roleRepository.existsById(id)) {
            throw new ResourceNotFoundException("Role not found with id: " + id);
        }
        roleRepository.deleteById(id);
    }

    /**
     * Add permission to role role dto.
     *
     * @param roleId       the role id
     * @param permissionId the permission id
     * @return the role dto
     */
    @Transactional
    public RoleDTO addPermissionToRole(Long roleId, Long permissionId) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with id: " + roleId));

        Permission permission = permissionRepository.findById(permissionId)
                .orElseThrow(() -> new ResourceNotFoundException("Permission not found with id: " + permissionId));

        role.getPermissions().add(permission);
        Role savedRole = roleRepository.save(role);
        return roleMapper.toDto(savedRole);
    }

    /**
     * Remove permission from role role dto.
     *
     * @param roleId       the role id
     * @param permissionId the permission id
     * @return the role dto
     */
    @Transactional
    public RoleDTO removePermissionFromRole(Long roleId, Long permissionId) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with id: " + roleId));

        Permission permission = permissionRepository.findById(permissionId)
                .orElseThrow(() -> new ResourceNotFoundException("Permission not found with id: " + permissionId));

        role.getPermissions().remove(permission);
        Role savedRole = roleRepository.save(role);
        return roleMapper.toDto(savedRole);
    }
}