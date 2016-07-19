package be.ttime.core.persistence.service;

import be.ttime.core.persistence.model.GroupEntity;
import be.ttime.core.persistence.model.RoleEntity;

import java.util.List;
import java.util.Map;

public interface IAuthorityService {

    GroupEntity findGroupByName(String name);
    GroupEntity findGroupById(Long id);
    RoleEntity findRoleByName(String name);
    RoleEntity findRoleById(Long id);

    List<GroupEntity> findAllGroup();
    List<RoleEntity> findAllRole();

    GroupEntity saveGroup(GroupEntity group);
    List<GroupEntity> saveGroups(List<GroupEntity> groups);

    RoleEntity saveRole(RoleEntity role);
    List<RoleEntity> saveRoles(List<RoleEntity> roles);

    List<GroupEntity> findAllClientGroup();

    String jsonAdminGroup();

    Map<String, List<RoleEntity>> getRoleByGroup();

    void deleteGroup(Long id);

}
