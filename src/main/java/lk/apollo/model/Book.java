package lk.apollo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

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

    private Integer pageCount;

    private String language;
}
