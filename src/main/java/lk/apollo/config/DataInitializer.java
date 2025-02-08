package lk.apollo.config;

import jakarta.annotation.PostConstruct;
import lk.apollo.model.Genre;
import lk.apollo.repository.GenreRepository;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class DataInitializer {

    private final GenreRepository genreRepository;

    public DataInitializer(GenreRepository genreRepository) {
        this.genreRepository = genreRepository;
    }

    @PostConstruct
    public void init() {
        // List of default genres
        Set<String> defaultGenres = Set.of("Fiction", "Non-Fiction", "Mystery", "Romance", "Science Fiction", "Fantasy", "Biography");

        // Save only if the genre doesn't already exist
        defaultGenres.forEach(name -> {
            if (!genreRepository.existsByName(name)) {
                genreRepository.save(new Genre(name, new HashSet<>()));
            }
        });
    }
}
