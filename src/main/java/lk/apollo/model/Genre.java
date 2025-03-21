package lk.apollo.model;

import jakarta.persistence.*;

@Entity
public class Genre {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "genre_id_seq")
    @SequenceGenerator(name = "genre_id_seq", sequenceName = "genre_id_seq", allocationSize = 1)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name; // Store as String instead of GenreEnum

    public Genre() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
