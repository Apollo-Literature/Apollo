package lk.apollo.dto;

import java.util.Set;

public class GenreDTO {

    private Long genreId;
    private String name;
    private Set<String> books; // You can add a list of books' titles, or any other relevant book info

    public GenreDTO() {
    }

    public GenreDTO(Long genreId, String name, Set<String> books) {
        this.genreId = genreId;
        this.name = name;
        this.books = books;
    }

    public Long getGenreId() {
        return genreId;
    }

    public void setGenreId(Long genreId) {
        this.genreId = genreId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<String> getBooks() {
        return books;
    }

    public void setBooks(Set<String> books) {
        this.books = books;
    }
}
