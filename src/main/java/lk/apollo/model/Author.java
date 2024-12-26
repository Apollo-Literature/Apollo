package lk.apollo.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@NoArgsConstructor
@Data
@Entity
@Table(name = "authors")
public class Author {

    @Id
    @SequenceGenerator(
            name = "author_id_seq",
            sequenceName = "author_id_seq"
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "author_id_seq"
    )
    private Long authorId; // Primary key for the author

    private String firstName;

    private String lastName;

    private String email;

    private String companyName;

    private LocalDate birthDate;

    private String nationality;

    // One-to-many relationship with books. An author can write multiple books.
    @OneToMany(mappedBy = "author")
    private List<Book> books;
}
