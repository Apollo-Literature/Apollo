package lk.apollo.util;

import jakarta.annotation.PostConstruct;
import lk.apollo.model.Permission;
import lk.apollo.model.Role;
import lk.apollo.repository.PermissionRepository;
import lk.apollo.repository.RoleRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class DataInitializer {
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    public DataInitializer(RoleRepository roleRepository, PermissionRepository permissionRepository) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
    }

    @PostConstruct
    @Transactional
    public void initialize() {
        initializePermissions();
        initializeRoles();
    }

    private void initializePermissions() {
        List<String> permissionNames = Arrays.asList(
                Constants.PERM_READ,
                Constants.PERM_WRITE,
                Constants.PERM_DELETE,
                Constants.PERM_ADMIN
        );

        for (String name : permissionNames) {
            if (!permissionRepository.existsByName(name)) {
                Permission permission = new Permission();
                permission.setName(name);
                permissionRepository.save(permission);
            }
        }
    }

    private void initializeRoles() {
        // Admin Role
        if (!roleRepository.existsByName(RoleEnum.ADMIN.name())) {
            Role adminRole = new Role();
            adminRole.setName(RoleEnum.ADMIN.name());

            Set<Permission> adminPermissions = new HashSet<>(permissionRepository.findAll());
            adminRole.setPermissions(adminPermissions);

            roleRepository.save(adminRole);
        }

        // Publisher Role
        if (!roleRepository.existsByName(RoleEnum.PUBLISHER.name())) {
            Role publisherRole = new Role();
            publisherRole.setName(RoleEnum.PUBLISHER.name());

            Set<Permission> publisherPermissions = new HashSet<>();
            permissionRepository.findByName(Constants.PERM_READ).ifPresent(publisherPermissions::add);
            permissionRepository.findByName(Constants.PERM_WRITE).ifPresent(publisherPermissions::add);

            publisherRole.setPermissions(publisherPermissions);

            roleRepository.save(publisherRole);
        }

        // Reader Role
        if (!roleRepository.existsByName(RoleEnum.READER.name())) {
            Role readerRole = new Role();
            readerRole.setName(RoleEnum.READER.name());

            Set<Permission> readerPermissions = new HashSet<>();
            permissionRepository.findByName(Constants.PERM_READ).ifPresent(readerPermissions::add);

            readerRole.setPermissions(readerPermissions);

            roleRepository.save(readerRole);
        }
    }
}