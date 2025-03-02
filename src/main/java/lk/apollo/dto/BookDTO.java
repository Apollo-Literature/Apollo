package lk.apollo.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

public class BookDTO implements Serializable {
    private Long bookId;
    private String title;
    private String description;
    private String isbn;
    private LocalDate publicationDate;
    private Integer pageCount;
    private String language;
    private BigDecimal price;
    private String thumbnail;
    private String url;

    public BookDTO() {}

    public BookDTO(String title, String description, String isbn, LocalDate publicationDate, Integer pageCount, String language, BigDecimal price, String thumbnail, String url) {
        this.title = title;
        this.description = description;
        this.isbn = isbn;
        this.publicationDate = publicationDate;
        this.pageCount = pageCount;
        this.language = language;
        this.price = price;
        this.thumbnail = thumbnail;
        this.url = url;
    }

    public BookDTO(Long bookId, String title, String description, String isbn, LocalDate publicationDate, Integer pageCount, String language, BigDecimal price, String thumbnail, String url) {
        this.bookId = bookId;
        this.title = title;
        this.description = description;
        this.isbn = isbn;
        this.publicationDate = publicationDate;
        this.pageCount = pageCount;
        this.language = language;
        this.price = price;
        this.thumbnail = thumbnail;
        this.url = url;
    }

    public Long getBookId() {
        return bookId;
    }

    public void setBookId(Long bookId) {
        this.bookId = bookId;
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

    public Integer getPageCount() {
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

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
