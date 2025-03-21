package lk.apollo.util;

import lk.apollo.model.Genre;
import lk.apollo.repository.GenreRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class GenreDataInitializer implements CommandLineRunner {

    private final GenreRepository genreRepository;

    public GenreDataInitializer(GenreRepository genreRepository) {
        this.genreRepository = genreRepository;
    }

    @Override
    public void run(String... args) {
        Arrays.stream(GenreEnum.values()).forEach(genreEnum -> {
            String genreName = genreEnum.name(); // Convert GenreEnum to String
            if (genreRepository.findByName(genreName).isEmpty()) {
                genreRepository.save(new Genre(genreName)); // Pass String instead of Enum
            }
        });
    }
}
