package lk.apollo.dto;

import lk.apollo.util.GenreEnum;

public class GenreDTO {
    private Long id;
    private GenreEnum name;

    public GenreDTO() {}

    public GenreDTO(Long id, GenreEnum name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public GenreEnum getName() {
        return name;
    }

    public void setName(GenreEnum name) {
        this.name = name;
    }
}
