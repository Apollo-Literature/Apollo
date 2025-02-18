package lk.apollo.dto;

import lk.apollo.model.Author;
import lk.apollo.model.Genre;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

//! Cannot have primitive datatypes
//TODO: NoArgs, AllArgs, Serializable
public class BookDTO implements Serializable {
    private String title;
    private String description;
    private String isbn;
    private LocalDate publicationDate;
    private Integer pageCount;
    private String language;
    private BigDecimal price;
    private Author author; // Reference by ID instead of entity
    private Set<GenreDTO> genre; // Reference by ID instead of entity
    private String url;

    public BookDTO(String title, String description, String isbn, LocalDate publicationDate, Integer pageCount, String language, BigDecimal price, Author authorId, Set<GenreDTO> genre, String url) {
        this.title = title;
        this.description = description;
        this.isbn = isbn;
        this.publicationDate = publicationDate;
        this.pageCount = pageCount;
        this.language = language;
        this.price = price;
        this.author = authorId;
        this.genre = genre;
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

    public void setPageCount(Integer pageCount) {
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

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }

    public Set<GenreDTO> getGenre() {
        return genre;
    }

    public void setGenre(Set<GenreDTO> genre) {
        this.genre = genre;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
