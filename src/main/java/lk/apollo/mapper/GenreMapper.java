package lk.apollo.mapper;

import lk.apollo.dto.GenreDTO;
import lk.apollo.model.Genre;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

@Mapper(componentModel = "spring")
public interface GenreMapper {

    /**
     * Map GenreType entity to GenreDTO
     *
     * @param genre - GenreType entity
     * @return GenreDTO instance
     */
    @Mapping(source = "books", target = "books")
    GenreDTO mapToDTO(Genre genre);

    /**
     * Map GenreDTO to GenreType entity
     *
     * @param genreDTO - GenreDTO instance
     * @return GenreType entity
     */
    @Mapping(source = "books", target = "books")
    Genre mapToEntity(GenreDTO genreDTO);
}
