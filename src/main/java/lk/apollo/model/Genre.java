package lk.apollo.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@NoArgsConstructor
@Data
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
}
