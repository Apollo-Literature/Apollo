package lk.apollo.dto;

import java.io.Serializable;

public class PermissionDTO implements Serializable {
    private Long id;
    private String name;

    public PermissionDTO() {}

    // Getters and Setters

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
