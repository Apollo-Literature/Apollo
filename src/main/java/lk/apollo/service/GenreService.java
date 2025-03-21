package lk.apollo.service;

import lk.apollo.dto.GenreDTO;
import lk.apollo.util.GenreEnum;
import lk.apollo.model.Genre;
import lk.apollo.repository.GenreRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GenreService {

    private final GenreRepository genreRepository;

    public GenreService(GenreRepository genreRepository) {
        this.genreRepository = genreRepository;
    }

    /**
     * Get all genres
     * @return List of GenreDTO instances
     */
    public List<GenreDTO> getAllGenres() {
        return genreRepository.findAll().stream()
                .map(genre -> new GenreDTO(genre.getId(), genre.getName()))
                .collect(Collectors.toList());
    }

    /**
     * Add a new genre
     * @param genreDTO
     * @return GenreDTO instance
     */
    public GenreDTO addGenre(GenreDTO genreDTO) {
        if (genreRepository.findByName(genreDTO.getName()).isPresent()) {
            throw new IllegalArgumentException("Genre already exists.");
        }

        Genre genre = new Genre(genreDTO.getName());
        Genre savedGenre = genreRepository.save(genre);
        return new GenreDTO(savedGenre.getId(), savedGenre.getName());
    }
}
