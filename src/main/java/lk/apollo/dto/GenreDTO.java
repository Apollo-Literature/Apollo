package lk.apollo.dto;

import java.util.Set;

public class GenreDTO {

    private String name;
    private Set<Long> bookIds; // Reference book IDs instead of full book objects

    public GenreDTO() {
    }

    public GenreDTO(String name, Set<Long> bookIds) {
        this.name = name;
        this.bookIds = bookIds;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Long> getBookIds() {
        return bookIds;
    }

    public void setBookIds(Set<Long> bookIds) {
        this.bookIds = bookIds;
    }
}
