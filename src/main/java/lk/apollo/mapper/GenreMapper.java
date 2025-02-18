package lk.apollo.mapper;

import lk.apollo.dto.GenreDTO;
import lk.apollo.model.Genre;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

@Mapper(componentModel = "spring")
public interface GenreMapper {

    /**
     * Map Genre entity to GenreDTO
     *
     * @param genre - Genre entity
     * @return GenreDTO instance
     */
    @Mapping(source = "books", target = "books")
    GenreDTO mapToDTO(Genre genre);

    /**
     * Map GenreDTO to Genre entity
     *
     * @param genreDTO - GenreDTO instance
     * @return Genre entity
     */
    @Mapping(source = "books", target = "books")
    Genre mapToEntity(GenreDTO genreDTO);
}
