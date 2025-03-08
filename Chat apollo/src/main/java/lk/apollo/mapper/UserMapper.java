package lk.apollo.mapper;

import lk.apollo.dto.UserDTO;
import lk.apollo.model.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDTO mapToDTO(User user);

    User mapToEntity(UserDTO userDTO);
}
