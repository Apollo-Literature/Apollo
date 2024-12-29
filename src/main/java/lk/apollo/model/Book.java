package lk.apollo.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Component
@NoArgsConstructor
@Data
@Entity
@Table(name = "books")
public class Book {

    @Id
    @SequenceGenerator( // A sequence generator
            name = "ticket_id",
            sequenceName = "ticket_id"
    )
    @GeneratedValue( // adding generated value
            strategy = GenerationType.SEQUENCE,
            generator = "ticket_id"
    )
    private Long bookId;

    private String title;

    @Column(unique = true)
    private String isbn;

    private LocalDate publicationDate;

    private int pageCount;

    private String language;

    private BigDecimal price;

    // Many-to-one relationship with authors. A book is written by one author.
    @ManyToOne
    @JoinColumn(name = "author_id", nullable = false)
    private Author author;

    // Many-to-one relationship with publishers. A book is published by one publisher.
    @ManyToOne
    @JoinColumn(name = "publisher_id", nullable = false)
    private Publisher publisher;

    // Many-to-many relationship with genres, using a join table `books_genres`.
    @ManyToMany
    @JoinTable(
            name = "books_genres",
            joinColumns = @JoinColumn(name = "book_id"),
            inverseJoinColumns = @JoinColumn(name = "genre_id")
    )
    private Set<Genre> genres;

    // One-to-many relationship with reviews. A book can have multiple reviews.
    @OneToMany(mappedBy = "book")
    private List<Review> reviews;

    @Lob // JPA annotation to map to a large object type in the database
    private String url;
}
