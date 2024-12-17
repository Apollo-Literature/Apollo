package lk.apollo.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@NoArgsConstructor
@Data
@Entity
@Table(name = "authors")
public class Author {

    @Id
    @SequenceGenerator(
            name = "ticket_id",
            sequenceName = "ticket_id"
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "ticket_id"
    )
    private Long authorId;

    private String name;

    private LocalDate birthDate;

    private String nationality;

}
