package lk.apollo.config;

import org.springframework.stereotype.Component;

@Component
public class DataInitializer {

//    private final GenreRepository genreRepository;
//    private final RoleRepository roleRepository;
//
//    public DataInitializer(GenreRepository genreRepository, RoleRepository roleRepository) {
//        this.genreRepository = genreRepository;
//        this.roleRepository = roleRepository;
//    }
//
//    @PostConstruct
//    public void init() {
//        // Initialize genres
//        for (GenreType genreTypeEnum : GenreType.values()) {
//            String genreName = genreTypeEnum.name().replace("_", " ");
//            if (!genreRepository.existsByName(genreName)) {
//                genreRepository.save(new Genre(genreName, new HashSet<>()));
//            }
//        }
//
//        // Initialize roles
//        for (RoleType roleTypeEnum : RoleType.values()) {
//            String roleName = roleTypeEnum.name();
//            if (!roleRepository.existsByName(roleName)) {
//                roleRepository.save(new Role(roleName));
//            }
//        }
//    }
}
