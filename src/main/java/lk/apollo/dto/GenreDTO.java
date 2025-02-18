package lk.apollo.dto;

import java.io.Serializable;
import java.util.Set;

public class GenreDTO implements Serializable {

    private String name;
    private Set<BookDTO> books; // Reference book IDs instead of full book objects

    public GenreDTO() {
    }

    public GenreDTO(String name, Set<BookDTO> books) {
        this.name = name;
        this.books = books;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<BookDTO> getBooks() {
        return books;
    }

    public void setBooks(Set<BookDTO> books) {
        this.books = books;
    }
}
