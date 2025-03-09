package lk.apollo.mapper;

import lk.apollo.dto.UserDTO;
import lk.apollo.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring", uses = {RoleMapper.class})
public interface UserMapper {
    UserDTO toDto(User user);

    User toEntity(UserDTO userDTO);

    List<UserDTO> toDtoList(List<User> users);

    void updateUserFromDto(UserDTO userDTO, @MappingTarget User user);
}