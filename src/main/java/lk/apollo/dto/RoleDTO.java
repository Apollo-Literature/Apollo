package lk.apollo.dto;

import java.io.Serializable;
import java.util.Set;

public class RoleDTO implements Serializable {
    private Long roleId;
    private String name;
    private Set<PermissionDTO> permissions;

    public RoleDTO() {}

    // Getters and Setters


    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<PermissionDTO> getPermissions() {
        return permissions;
    }

    public void setPermissions(Set<PermissionDTO> permissions) {
        this.permissions = permissions;
    }
}
