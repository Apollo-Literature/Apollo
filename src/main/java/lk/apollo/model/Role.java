package lk.apollo.model;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "roles")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "role_id_seq")
    @SequenceGenerator(name = "role_id_seq", sequenceName = "role_id_seq", allocationSize = 1)
    private Long roleId;
    @Column(nullable = false, unique = true)
    private String name;
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "role_permissions",
            joinColumns = @JoinColumn(name = "role_id"),
            inverseJoinColumns = @JoinColumn(name = "permission_id")
    )
    private Set<Permission> permissions = new HashSet<>();
    @ManyToMany(mappedBy = "roles")
    private Set<User> users = new HashSet<>();


    //Constructors
    public Role() {}

    public Role(Long roleId, String name, Set<Permission> permissions, Set<User> users) {
        this.roleId = roleId;
        this.name = name;
        this.permissions = permissions;
        this.users = users;
    }

    //Getters and Setters
    public Long getRoleId() { return roleId; }
    public void setRoleId(Long id) { this.roleId = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Set<Permission> getPermissions() { return permissions; }
    public void setPermissions(Set<Permission> permissions) { this.permissions = permissions; }

    public Set<User> getUsers() {
        return users;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }
}
