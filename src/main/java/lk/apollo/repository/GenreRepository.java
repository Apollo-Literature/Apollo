package lk.apollo.repository;

import lk.apollo.model.Genre;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GenreRepository extends JpaRepository<Genre, Long> {
    boolean existsByName(String name);
    Optional<Genre> findByName(String name); // Change GenreEnum to String
}
