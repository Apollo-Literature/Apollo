package lk.apollo.mapper;

import lk.apollo.dto.RoleDTO;
import lk.apollo.model.Role;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring", uses = {PermissionMapper.class})
public interface RoleMapper {
    RoleDTO toDto(Role role);

    Role toEntity(RoleDTO roleDTO);

    List<RoleDTO> toDtoList(List<Role> roles);

    Set<RoleDTO> toDtoSet(Set<Role> roles);

    Set<Role> toEntitySet(Set<RoleDTO> roleDTOs);

    void updateRoleFromDto(RoleDTO roleDTO, @MappingTarget Role role);
}