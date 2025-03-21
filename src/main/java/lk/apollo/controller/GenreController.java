package lk.apollo.controller;

import lk.apollo.dto.GenreDTO;
import lk.apollo.service.GenreService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/genres")
public class GenreController {

    private final GenreService genreService;

    public GenreController(GenreService genreService) {
        this.genreService = genreService;
    }

    /**
     * Get all genres
     *
     * @return List of GenreDTO instances
     */
    @GetMapping
    public ResponseEntity<List<String>> getAllGenres() {
        return ResponseEntity.ok(genreService.getAllGenres());
    }

    /**
     * Add a new genre
     * @param genreDTO
     * @return GenreDTO instance
     */
    @PostMapping
    public ResponseEntity<GenreDTO> addGenre(@RequestBody GenreDTO genreDTO) {
        return ResponseEntity.ok(genreService.addGenre(genreDTO));
    }
}
