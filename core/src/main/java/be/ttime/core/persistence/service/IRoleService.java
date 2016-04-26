package be.ttime.core.persistence.service;

import be.ttime.core.persistence.model.PrivilegeEntity;
import be.ttime.core.persistence.model.RoleEntity;

import java.util.List;

public interface IRoleService {

    RoleEntity findRoleByName(String name);
    PrivilegeEntity findPrivilegeByName(String name);
    List<RoleEntity> findAll();
    RoleEntity saveRole(RoleEntity role);
    List<RoleEntity> saveRoles(List<RoleEntity> roles);
    PrivilegeEntity savePrivilege(PrivilegeEntity privilege);
    List<PrivilegeEntity> savePrivileges(List<PrivilegeEntity> privileges);
}
