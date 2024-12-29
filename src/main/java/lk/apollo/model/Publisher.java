package lk.apollo.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@NoArgsConstructor
@Data
@Entity
@Table(name = "publishers")
public class Publisher {

    @Id
    @SequenceGenerator(
            name = "publisher_id_seq",
            sequenceName = "publisher_id_seq"
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "publisher_id_seq"
    )
    private Long publisherId; // Primary key for the publisher

    private String name;

    // One-to-many relationship with books. A publisher can publish multiple books.
    @OneToMany(mappedBy = "publisher")
    private List<Book> books;
}