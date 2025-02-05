package lk.apollo.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Set;

@NoArgsConstructor
@Entity
@Table(name = "genres")
public class Genre {

    @Id
    @SequenceGenerator(
            name = "genre_id_seq",
            sequenceName = "genre_id_seq"
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "genre_id_seq"
    )
    private Long genreId; // Primary key for the genre

    private String name;

    // Many-to-many relationship with books, handled through the `books_genres` join table.
    @ManyToMany(mappedBy = "genres")
    private Set<Book> books;

    public Genre(String name, Set<Book> books) {
        this.name = name;
        this.books = books;
    }

    public Long getGenreId() {
        return genreId;
    }

    public void setGenreId(Long genreId) {
        this.genreId = genreId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Book> getBooks() {
        return books;
    }

    public void setBooks(Set<Book> books) {
        this.books = books;
    }
}
