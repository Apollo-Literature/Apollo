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
@Table(name = "reviews")
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reviewId; // Primary key for the review

    @ManyToOne
    @JoinColumn(name = "book_id", nullable = false)
    private Book book; // Owning side of the relationship

    private String review;

    private int rating;

    private LocalDate reviewDate;
}
