package lk.apollo.service;

import lk.apollo.dto.PermissionDTO;
import lk.apollo.exception.ResourceAlreadyExistsException;
import lk.apollo.exception.ResourceNotFoundException;
import lk.apollo.mapper.PermissionMapper;
import lk.apollo.model.Permission;
import lk.apollo.repository.PermissionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * The type Permission service.
 */
@Service
public class PermissionService {
    private final PermissionRepository permissionRepository;
    private final PermissionMapper permissionMapper;

    /**
     * Instantiates a new Permission service.
     *
     * @param permissionRepository the permission repository
     * @param permissionMapper     the permission mapper
     */
    public PermissionService(PermissionRepository permissionRepository, PermissionMapper permissionMapper) {
        this.permissionRepository = permissionRepository;
        this.permissionMapper = permissionMapper;
    }

    /**
     * Gets all permissions.
     *
     * @return the all permissions
     */
    @Transactional(readOnly = true)
    public List<PermissionDTO> getAllPermissions() {
        List<Permission> permissions = permissionRepository.findAll();
        return permissionMapper.toDtoList(permissions);
    }

    /**
     * Gets permission by id.
     *
     * @param id the id
     * @return the permission by id
     */
    @Transactional(readOnly = true)
    public PermissionDTO getPermissionById(Long id) {
        Permission permission = permissionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Permission not found with id: " + id));
        return permissionMapper.toDto(permission);
    }

    /**
     * Gets permission by name.
     *
     * @param name the name
     * @return the permission by name
     */
    @Transactional(readOnly = true)
    public PermissionDTO getPermissionByName(String name) {
        Permission permission = permissionRepository.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException("Permission not found with name: " + name));
        return permissionMapper.toDto(permission);
    }

    /**
     * Create permission permission dto.
     *
     * @param permissionDTO the permission dto
     * @return the permission dto
     */
    @Transactional
    public PermissionDTO createPermission(PermissionDTO permissionDTO) {
        if (permissionRepository.existsByName(permissionDTO.getName())) {
            throw new ResourceAlreadyExistsException("Permission already exists with name: " + permissionDTO.getName());
        }

        Permission permission = permissionMapper.toEntity(permissionDTO);
        permission = permissionRepository.save(permission);
        return permissionMapper.toDto(permission);
    }

    /**
     * Update permission permission dto.
     *
     * @param permissionDTO the permission dto
     * @return the permission dto
     */
    @Transactional
    public PermissionDTO updatePermission(PermissionDTO permissionDTO) {
        Permission existingPermission = permissionRepository.findById(permissionDTO.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Permission not found with id: " + permissionDTO.getId()));

        if (!existingPermission.getName().equals(permissionDTO.getName()) && permissionRepository.existsByName(permissionDTO.getName())) {
            throw new ResourceAlreadyExistsException("Permission already exists with name: " + permissionDTO.getName());
        }

        permissionMapper.updatePermissionFromDto(permissionDTO, existingPermission);
        Permission savedPermission = permissionRepository.save(existingPermission);
        return permissionMapper.toDto(savedPermission);
    }

    /**
     * Delete permission.
     *
     * @param id the id
     */
    @Transactional
    public void deletePermission(Long id) {
        if (!permissionRepository.existsById(id)) {
            throw new ResourceNotFoundException("Permission not found with id: " + id);
        }
        permissionRepository.deleteById(id);
    }
}