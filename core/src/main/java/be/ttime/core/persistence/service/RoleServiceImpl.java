package be.ttime.core.persistence.service;

import be.ttime.core.persistence.model.PrivilegeEntity;
import be.ttime.core.persistence.model.RoleEntity;
import be.ttime.core.persistence.repository.IPrivilegeRepository;
import be.ttime.core.persistence.repository.IRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class RoleServiceImpl implements IRoleService{

    @Autowired
    private IRoleRepository roleRepository;

    @Autowired
    private IPrivilegeRepository privilegeRepository;

    @Override
    public RoleEntity findRoleByName(String name) {
        return roleRepository.findByName(name);
    }

    @Override
    public PrivilegeEntity findPrivilegeByName(String name) {
        return privilegeRepository.findByName(name);
    }

    @Override
    public List<RoleEntity> findAll() {
        return roleRepository.findAll();
    }

    @Override
    public RoleEntity saveRole(RoleEntity role) {
        return roleRepository.save(role);
    }

    @Override
    public List<RoleEntity> saveRoles(List<RoleEntity> roles) {
        return roleRepository.save(roles);
    }

    @Override
    public PrivilegeEntity savePrivilege(PrivilegeEntity privilege) {
        return privilegeRepository.save(privilege);
    }

    @Override
    public List<PrivilegeEntity> savePrivileges(List<PrivilegeEntity> privileges) {
        return privilegeRepository.save(privileges);
    }
}
