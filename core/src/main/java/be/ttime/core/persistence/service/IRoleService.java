package be.ttime.core.persistence.service;

import be.ttime.core.persistence.model.PrivilegeEntity;
import be.ttime.core.persistence.model.RoleEntity;

import java.util.List;
import java.util.Map;

public interface IRoleService {

    RoleEntity findRoleByName(String name);
    RoleEntity findRoleById(Long id);
    PrivilegeEntity findPrivilegeByName(String name);
    PrivilegeEntity findPrivilegeById(Long id);

    List<RoleEntity> findAllRole();
    List<PrivilegeEntity> findAllPrivilege();

    RoleEntity saveRole(RoleEntity role);
    List<RoleEntity> saveRoles(List<RoleEntity> roles);

    PrivilegeEntity savePrivilege(PrivilegeEntity privilege);
    List<PrivilegeEntity> savePrivileges(List<PrivilegeEntity> privileges);

    List<RoleEntity> findAllClientRole();

    String jsonAdminGroup();

    Map<String, List<PrivilegeEntity>> getPrivelegeByGroup();
}
