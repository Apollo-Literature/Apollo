package lk.apollo.mapper;

import lk.apollo.dto.GenreDTO;
import lk.apollo.model.Genre;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface GenreMapper {

    GenreMapper INSTANCE = Mappers.getMapper(GenreMapper.class);

    @Mapping(source = "name", target = "name")
    GenreDTO toDTO(Genre genre);

    @Mapping(source = "name", target = "name")
    Genre toEntity(GenreDTO genreDTO);
}