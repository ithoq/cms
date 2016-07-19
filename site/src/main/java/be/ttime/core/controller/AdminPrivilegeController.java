package be.ttime.core.controller;

import be.ttime.core.error.ResourceNotFoundException;
import be.ttime.core.persistence.model.PrivilegeEntity;
import be.ttime.core.persistence.model.RoleEntity;
import be.ttime.core.persistence.service.IRoleService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashSet;
import java.util.Set;

@Controller
@RequestMapping(value = "/admin/group")
@Slf4j
public class AdminPrivilegeController {

    @Autowired
    private IRoleService roleService;
    private final static String VIEWPATH = "admin/group/";


    @RequestMapping(value = "", method = RequestMethod.GET)
    public String home(ModelMap model) {
        return VIEWPATH + "home";
    }

    @RequestMapping(value = "/getJson", method = RequestMethod.GET)
    @ResponseBody
    public String getjson(HttpServletResponse response) {

        return roleService.jsonAdminGroup();
    }

    @RequestMapping(value = "/edit", method = RequestMethod.GET)
    public String edit(ModelMap model) {
        return edit(null, model);
    }

    @RequestMapping(value = "/edit/{id}", method = RequestMethod.GET)
    public String edit(@PathVariable("id") Long id, ModelMap model) {
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().disableHtmlEscaping().create();
        if (id != null) {
            RoleEntity role = roleService.findRoleById(id);

            if (role == null) {
                throw new ResourceNotFoundException("Role with id \" + id + \" not found!\"");

            }

            model.put("role", role);
        }
        model.put("privileges", roleService.getPrivelegeByGroup());
        return VIEWPATH + "edit";
    }

    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    public String edit(ModelMap model, Long id, String name, String description, Long[] privileges, HttpServletRequest request) throws ResourceNotFoundException {
        RoleEntity role;
        if(id != null){
            role = roleService.findRoleById(id);

            if(role == null){
                throw new ResourceNotFoundException("User with id \" + id + \" not found!\"");
            }
        } else{
            role = new RoleEntity();
        }

        Set<PrivilegeEntity> privilegeEntityList = new HashSet<>();
        PrivilegeEntity privilegeEntity;
        for (Long privilegeId : privileges) {
            privilegeEntity = roleService.findPrivilegeById(privilegeId);
            if(privilegeEntity==null)
                throw new IllegalArgumentException("privilege with id " + privilegeId + " is not found");
            privilegeEntityList.add(privilegeEntity);
        }
        role.setName(name);
        role.setDescription(description);
        role.setPrivileges(privilegeEntityList);
        role = roleService.saveRole(role);
        return "redirect:/admin/group/edit/" + role.getId() ;
    }
}