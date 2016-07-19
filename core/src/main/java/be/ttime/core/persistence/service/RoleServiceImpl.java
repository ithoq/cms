package be.ttime.core.persistence.service;

import be.ttime.core.persistence.model.PrivilegeEntity;
import be.ttime.core.persistence.model.RoleEntity;
import be.ttime.core.persistence.repository.IPrivilegeRepository;
import be.ttime.core.persistence.repository.IRoleRepository;
import be.ttime.core.util.CmsUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class RoleServiceImpl implements IRoleService{

    @Autowired
    private IRoleRepository roleRepository;

    @Autowired
    private IPrivilegeRepository privilegeRepository;

    @Override
    public RoleEntity findRoleById(Long id) {
        return roleRepository.findOne(id);
    }

    @Override
    public RoleEntity findRoleByName(String name) {
        return roleRepository.findByName(name);
    }

    @Override
    public PrivilegeEntity findPrivilegeById(Long id) {
        return privilegeRepository.findOne(id);
    }

    @Override
    public PrivilegeEntity findPrivilegeByName(String name) {
        return privilegeRepository.findByName(name);
    }


    @Override
    public List<RoleEntity> findAllRole() {
        return roleRepository.findAll();
    }

    @Override
    public List<PrivilegeEntity> findAllPrivilege() {
        return privilegeRepository.findAll();
    }

    @Override
    public List<RoleEntity> findAllClientRole() {
        return roleRepository.findAllForClient("ROLE_SUPER_ADMIN");
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

    @Override
    public Map<String, List<PrivilegeEntity>> getPrivelegeByGroup(){
        List<PrivilegeEntity> privileges = privilegeRepository.findAll();
        Map<String,List<PrivilegeEntity>> result = new HashMap<>();
        for (PrivilegeEntity privilege : privileges) {
            List<PrivilegeEntity> list = result.get(privilege.getSection());
            if(list == null){
                list = new ArrayList<>();
            }
            list.add(privilege);
            result.put(privilege.getSection(), list);
        }
        return result;
    }

    @Override
    public String jsonAdminGroup() {

        JsonArrayBuilder data = Json.createArrayBuilder();
        JsonObjectBuilder row;

        for (RoleEntity role : findAllClientRole()) {
            row = Json.createObjectBuilder();
            row.add("DT_RowData", Json.createObjectBuilder().add("id", role.getId()));
            row.add("name", CmsUtils.emptyStringIfnull(role.getName()));
            row.add("description", CmsUtils.emptyStringIfnull(role.getDescription()));
            row.add("deletable", role.isDeletable());
            data.add(row);
        }

        return Json.createObjectBuilder().add("data", data).build().toString();
    }
}
