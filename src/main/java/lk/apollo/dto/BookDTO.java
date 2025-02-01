package lk.apollo.dto;

import lk.apollo.model.Author;
import lk.apollo.model.Genre;
import lk.apollo.model.Review;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@NoArgsConstructor
@Getter
@Setter
public class BookDTO {
    private String title;
    private String description;
    private String isbn;
    private LocalDate publicationDate;
    private int pageCount;
    private String language;
    private BigDecimal price;
    private Author author;
    private Set<Genre> genres;
    private List<Review> reviews;
    private String url;
}
