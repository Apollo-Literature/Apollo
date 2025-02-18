package lk.apollo.config;

import jakarta.annotation.PostConstruct;
import lk.apollo.model.Genre;
import lk.apollo.model.Role;
import lk.apollo.repository.GenreRepository;
import lk.apollo.repository.RoleRepository;
import org.springframework.stereotype.Component;

import java.util.HashSet;

@Component
public class DataInitializer {

    private final GenreRepository genreRepository;
    private final RoleRepository roleRepository;

    public DataInitializer(GenreRepository genreRepository, RoleRepository roleRepository) {
        this.genreRepository = genreRepository;
        this.roleRepository = roleRepository;
    }

    @PostConstruct
    public void init() {
        // Initialize genres
        for (lk.apollo.model.enums.Genre genreEnum : lk.apollo.model.enums.Genre.values()) {
            String genreName = genreEnum.name().replace("_", " ");
            if (!genreRepository.existsByName(genreName)) {
                genreRepository.save(new Genre(genreName, new HashSet<>()));
            }
        }

        // Initialize roles
        for (lk.apollo.model.enums.Role roleEnum : lk.apollo.model.enums.Role.values()) {
            String roleName = roleEnum.name();
            if (!roleRepository.existsByName(roleName)) {
                roleRepository.save(new Role(roleName));
            }
        }
    }
}
