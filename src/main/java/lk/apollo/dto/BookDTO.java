package lk.apollo.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;


public class BookDTO {
    private String title;
    private String description;
    private String isbn;
    private LocalDate publicationDate;
    private int pageCount;
    private String language;
    private BigDecimal price;
    private Long authorId; // Reference by ID instead of entity
    private Set<Long> genreIds; // Reference by ID instead of entity
    private String url;

    public BookDTO(String title, String description, String isbn, LocalDate publicationDate, int pageCount,
                   String language, BigDecimal price, Long authorId, Set<Long> genreIds, String url) {
        this.title = title;
        this.description = description;
        this.isbn = isbn;
        this.publicationDate = publicationDate;
        this.pageCount = pageCount;
        this.language = language;
        this.price = price;
        this.authorId = authorId;
        this.genreIds = genreIds;
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public LocalDate getPublicationDate() {
        return publicationDate;
    }

    public void setPublicationDate(LocalDate publicationDate) {
        this.publicationDate = publicationDate;
    }

    public int getPageCount() {
        return pageCount;
    }

    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Long getAuthorId() {
        return authorId;
    }

    public void setAuthorId(Long authorId) {
        this.authorId = authorId;
    }

    public Set<Long> getGenreIds() {
        return genreIds;
    }

    public void setGenreIds(Set<Long> genreIds) {
        this.genreIds = genreIds;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
