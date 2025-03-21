package lk.apollo.service;

import lk.apollo.dto.GenreDTO;
import lk.apollo.mapper.GenreMapper;
import lk.apollo.model.Genre;
import lk.apollo.repository.GenreRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GenreService {

    private final GenreRepository genreRepository;
    private final GenreMapper genreMapper;

    public GenreService(GenreRepository genreRepository, GenreMapper genreMapper) {
        this.genreRepository = genreRepository;
        this.genreMapper = genreMapper;
    }

    public List<String> getAllGenres() {
        return genreRepository.findAll().stream()
                .map(Genre::getName) // Send only name
                .collect(Collectors.toList());
    }

    public GenreDTO addGenre(GenreDTO genreDTO) {
        if (genreRepository.findByName(genreDTO.getName()).isPresent()) {
            throw new IllegalArgumentException("Genre already exists.");
        }

        Genre genre = genreMapper.toEntity(genreDTO);
        Genre savedGenre = genreRepository.save(genre);
        return genreMapper.toDTO(savedGenre);
    }
}
