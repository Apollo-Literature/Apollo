package lk.apollo.mapper;

import lk.apollo.dto.GenreDTO;
import lk.apollo.model.Genre;
import org.springframework.stereotype.Component;

@Component
public class GenreMapper {

    public GenreDTO toDTO(Genre genre) {
        if (genre == null) {
            return null;
        }
        return new GenreDTO(genre.getName()); // Just return the name as a string
    }

    public Genre toEntity(GenreDTO genreDTO) {
        if (genreDTO == null) {
            return null;
        }
        Genre genre = new Genre();
        genre.setName(genreDTO.getName()); // Directly set String name
        return genre;
    }
}
