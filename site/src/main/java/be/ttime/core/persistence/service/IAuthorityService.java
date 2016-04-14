package be.ttime.core.persistence.service;

import be.ttime.core.persistence.model.PrivilegeEntity;
import be.ttime.core.persistence.model.RoleEntity;

import java.util.Collection;

public interface IAuthorityService {

    RoleEntity addRole(RoleEntity role);
    PrivilegeEntity createPrivilegeIfNotFound(String name);
    PrivilegeEntity addPrivilege(PrivilegeEntity privilege);
    RoleEntity createRoleIfNotFound(String name, Collection<PrivilegeEntity> privileges);
    void deleteRole(Long id);
    void deleteRole(RoleEntity role);
    void deletePrivilege(Long id);
    void deletePrivilege(PrivilegeEntity privilege);

    RoleEntity findRoleByName(String name);

}
