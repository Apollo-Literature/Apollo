package lk.apollo.mapper;

import lk.apollo.dto.UserDTO;
import lk.apollo.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring", uses = {RoleMapper.class})
public interface UserMapper {
    UserDTO toDto(User user);

    User toEntity(UserDTO userDTO);

    List<UserDTO> toDtoList(List<User> users);

    void updateUserFromDto(UserDTO userDTO, @MappingTarget User user);

    // Add this new method that ignores the email field during mapping
    @Mapping(target = "email", ignore = true)
    @Mapping(target = "supabaseUserId", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    void updateUserFromDtoIgnoreEmail(UserDTO userDTO, @MappingTarget User user);
}