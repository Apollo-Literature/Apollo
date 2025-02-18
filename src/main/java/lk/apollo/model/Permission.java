package lk.apollo.model;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;

import java.util.Set;

@NoArgsConstructor
@Entity
@Table(name = "permissions")
public class Permission {

    @Id
    @SequenceGenerator(
            name = "permission_id_seq",
            sequenceName = "permission_id_seq",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "permission_id_seq"
    )
    private Long permissionId;

    private String name;

    @ManyToMany(mappedBy = "permissions")
    private Set<Role> roles;

    public Permission(String name) {
        this.name = name;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
