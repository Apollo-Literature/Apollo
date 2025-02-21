package lk.apollo.config;

import jakarta.annotation.PostConstruct;
import lk.apollo.model.Genre;
import lk.apollo.model.Role;
import lk.apollo.model.GenreType;
import lk.apollo.model.RoleType;
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
        for (GenreType genreTypeEnum : GenreType.values()) {
            String genreName = genreTypeEnum.name().replace("_", " ");
            if (!genreRepository.existsByName(genreName)) {
                genreRepository.save(new Genre(genreName, new HashSet<>()));
            }
        }

        // Initialize roles
        for (RoleType roleTypeEnum : RoleType.values()) {
            String roleName = roleTypeEnum.name();
            if (!roleRepository.existsByName(roleName)) {
                roleRepository.save(new Role(roleName));
            }
        }
    }
}
