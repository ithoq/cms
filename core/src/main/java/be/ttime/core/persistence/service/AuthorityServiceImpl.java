package be.ttime.core.persistence.service;

import be.ttime.core.persistence.model.PrivilegeEntity;
import be.ttime.core.persistence.model.RoleEntity;
import be.ttime.core.persistence.repository.IPrivilegeRepository;
import be.ttime.core.persistence.repository.IRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

@Service
@Transactional
public class AuthorityServiceImpl implements IAuthorityService {

    @Autowired
    private IRoleRepository roleRepository;
    @Autowired
    private IPrivilegeRepository privilegeRepository;

    @Override
    public RoleEntity addRole(RoleEntity role) {
        return roleRepository.save(role);
    }

    @Override
    public RoleEntity createRoleIfNotFound(String name, Collection<PrivilegeEntity> privileges) {
        RoleEntity role = roleRepository.findByName(name);
        if (role == null) {
            role = new RoleEntity(name);
            role.setPrivileges(privileges);
            roleRepository.save(role);
        }
        return role;
    }

    @Override
    public PrivilegeEntity addPrivilege(PrivilegeEntity privilege) {
        return privilegeRepository.save(privilege);
    }

    @Override
    public PrivilegeEntity createPrivilegeIfNotFound(String name) {
        PrivilegeEntity privilege = privilegeRepository.findByName(name);
        if (privilege == null) {
            privilege = new PrivilegeEntity(name);
            privilegeRepository.save(privilege);
        }
        return privilege;
    }

    @Override
    public void deleteRole(Long id) {
        roleRepository.delete(id);
    }

    @Override
    public void deleteRole(RoleEntity role) {
        roleRepository.delete(role);
    }


    @Override
    public void deletePrivilege(Long id) {
        privilegeRepository.delete(id);
    }

    @Override
    public void deletePrivilege(PrivilegeEntity privilege) {
        privilegeRepository.delete(privilege);
    }

    @Override
    public RoleEntity findRoleByName(String name) {
        return roleRepository.findByName(name);
    }
}
