package lk.apollo.model;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;

import java.util.Set;

@NoArgsConstructor
@Entity
@Table(name = "genres")
public class Genre {

    @Id
    @SequenceGenerator(
            name = "genre_id_seq",
            sequenceName = "genre_id_seq",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "genre_id_seq"
    )
    private Long genreId;

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
