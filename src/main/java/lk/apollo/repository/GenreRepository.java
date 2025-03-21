package lk.apollo.repository;

import lk.apollo.model.Genre;
import lk.apollo.util.GenreEnum;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GenreRepository extends JpaRepository<Genre, Long> {
    Optional<Genre> findByName(GenreEnum name);
}
