package lk.apollo.mapper;

import lk.apollo.model.Permission;
import lk.apollo.dto.PermissionDTO;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring")
public interface PermissionMapper {
    PermissionDTO toDto(Permission permission);

    Permission toEntity(PermissionDTO permissionDTO);

    List<PermissionDTO> toDtoList(List<Permission> permissions);

    Set<PermissionDTO> toDtoSet(Set<Permission> permissions);

    Set<Permission> toEntitySet(Set<PermissionDTO> permissionDTOs);

    void updatePermissionFromDto(PermissionDTO permissionDTO, @MappingTarget Permission permission);
}