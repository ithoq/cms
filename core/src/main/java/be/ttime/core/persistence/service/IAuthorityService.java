package be.ttime.core.persistence.service;

import be.ttime.core.persistence.model.GroupEntity;
import be.ttime.core.persistence.model.RoleEntity;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;
import java.util.Map;

public interface IAuthorityService {

    GroupEntity findGroupByName(String name);
    GroupEntity findGroupById(Long id);
    RoleEntity findRoleByName(String name);
    RoleEntity findRoleById(Long id);

    List<GroupEntity> findAllGroup();
    List<RoleEntity> findAllRole();

    @PreAuthorize("hasRole('ROLE_ADMIN_GROUP')")
    GroupEntity saveGroup(GroupEntity group);
    @PreAuthorize("hasRole('ROLE_ADMIN_GROUP')")
    List<GroupEntity> saveGroups(List<GroupEntity> groups);
    @PreAuthorize("hasRole('ROLE_ADMIN_GROUP')")
    RoleEntity saveRole(RoleEntity role);
    @PreAuthorize("hasRole('ROLE_ADMIN_GROUP')")
    List<RoleEntity> saveRoles(List<RoleEntity> roles);

    List<GroupEntity> findAllClientGroup();

    String jsonAdminGroup();

    Map<String, List<RoleEntity>> getRoleByGroup();

    @PreAuthorize("hasRole('ROLE_ADMIN_GROUP_DELETE')")
    void deleteGroup(Long id);

}
