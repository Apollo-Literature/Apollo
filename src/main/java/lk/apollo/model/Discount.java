package lk.apollo.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Set;

@Component
@NoArgsConstructor
@Data
@Entity
@Table(name = "discounts")
public class Discount {

    @Id
    @SequenceGenerator(
            name = "discount_id_seq",
            sequenceName = "discount_id_seq"
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "discount_id_seq"
    )
    private Long discountId; // Primary key for the discount

    private String name;  // Name of the discount (e.g., "Black Friday").

    private BigDecimal value; // Discount value (e.g., 10.00 for 10%).

    // Many-to-many relationship with books, handled through the `books_discounts` join table.
    @ManyToMany(mappedBy = "discounts")
    private Set<Book> books;
}
