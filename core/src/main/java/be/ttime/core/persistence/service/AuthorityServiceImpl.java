package be.ttime.core.persistence.service;

import be.ttime.core.persistence.model.GroupEntity;
import be.ttime.core.persistence.model.RoleEntity;
import be.ttime.core.persistence.repository.IGroupRepository;
import be.ttime.core.persistence.repository.IRoleRepository;
import be.ttime.core.util.CmsUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.security.access.AccessDeniedException;
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
public class AuthorityServiceImpl implements IAuthorityService {


    @Autowired
    private IGroupRepository groupRepository;

    @Autowired
    private IRoleRepository roleRepository;

    @Override
    public GroupEntity findGroupById(Long id) {
        return groupRepository.findOne(id);
    }

    @Override
    public GroupEntity findGroupByName(String name) {
        return groupRepository.findByName(name);
    }

    @Override
    public RoleEntity findRoleById(Long id) {
        return roleRepository.findOne(id);
    }

    @Override
    public RoleEntity findRoleByName(String name) {
        return roleRepository.findByName(name);
    }


    @Override
    public List<GroupEntity> findAllGroup() {
        return groupRepository.findAll();
    }

    @Override
    public List<RoleEntity> findAllRole() {
        return roleRepository.findAll();
    }

    @Override
    public List<GroupEntity> findAllClientGroup() {
        return groupRepository.findAllForClient(CmsUtils.GROUP_SUPER_ADMIN);
    }

    @Override
    @Caching(evict = { @CacheEvict(value = "user", allEntries = true) })
    public GroupEntity saveGroup(GroupEntity role) {
        return groupRepository.save(role);
    }

    @Override
    @Caching(evict = { @CacheEvict(value = "user", allEntries = true) })
    public List<GroupEntity> saveGroups(List<GroupEntity> roles) {
        return groupRepository.save(roles);
    }

    @Override
    @Caching(evict = { @CacheEvict(value = "user", allEntries = true) })
    public RoleEntity saveRole(RoleEntity privilege) {
        return roleRepository.save(privilege);
    }

    @Override
    @Caching(evict = { @CacheEvict(value = "user", allEntries = true) })
    public List<RoleEntity> saveRoles(List<RoleEntity> privileges) {
        return roleRepository.save(privileges);
    }

    @Override
    public Map<String, List<RoleEntity>> getRoleByGroup(){
        List<RoleEntity> privileges = roleRepository.findAll();
        Map<String,List<RoleEntity>> result = new HashMap<>();
        for (RoleEntity privilege : privileges) {
            List<RoleEntity> list = result.get(privilege.getSection());
            if(list == null){
                list = new ArrayList<>();
            }
            list.add(privilege);
            result.put(privilege.getSection(), list);
        }
        return result;
    }

    @Override
    public void deleteGroup(Long id) {
        GroupEntity group = groupRepository.findOne(id);
        if(group == null){
            throw new IllegalArgumentException();
        }
        if(!group.isDeletable()){
            throw new AccessDeniedException("Undeletable group");
        }

        groupRepository.delete(group);

    }

    @Override
    public String jsonAdminGroup() {

        JsonArrayBuilder data = Json.createArrayBuilder();
        JsonObjectBuilder row;

        for (GroupEntity group : findAllClientGroup()) {
            row = Json.createObjectBuilder();
            row.add("DT_RowData", Json.createObjectBuilder().add("id", group.getId()));
            row.add("name", CmsUtils.emptyStringIfnull(group.getName()));
            row.add("description", CmsUtils.emptyStringIfnull(group.getDescription()));
            row.add("deletable", group.isDeletable());
            data.add(row);
        }

        return Json.createObjectBuilder().add("data", data).build().toString();
    }

    /*
    @Override
    public GroupEntity addRole(GroupEntity role) {
        return groupRepository.save(role);
    }

    @Override
    public GroupEntity createRoleIfNotFound(String name, Collection<PrivilegeEntity> roles) {
        GroupEntity role = groupRepository.findByName(name);
        if (role == null) {
            role = new GroupEntity(name);
            for (PrivilegeEntity privilege : roles) {
                role.getRoles().add(privilege);
            }
            groupRepository.save(role);
        }
        return role;
    }

    @Override
    public PrivilegeEntity addPrivilege(PrivilegeEntity privilege) {
        return roleRepository.save(privilege);
    }

    @Override
    public PrivilegeEntity createPrivilegeIfNotFound(String name) {
        PrivilegeEntity privilege = roleRepository.findByName(name);
        if (privilege == null) {
            privilege = new PrivilegeEntity(name);
            roleRepository.save(privilege);
        }
        return privilege;
    }

    @Override
    public void deleteRole(Long id) {
        groupRepository.delete(id);
    }

    @Override
    public void deleteRole(GroupEntity role) {
        groupRepository.delete(role);
    }


    @Override
    public void deletePrivilege(Long id) {
        roleRepository.delete(id);
    }

    @Override
    public void deletePrivilege(PrivilegeEntity privilege) {
        roleRepository.delete(privilege);
    }

    @Override
    public GroupEntity findGroupByName(String name) {
        return groupRepository.findByName(name);
    }
    */
}
