package be.ttime.core.controller;

import be.ttime.core.error.ResourceNotFoundException;
import be.ttime.core.model.RedirectMessage;
import be.ttime.core.persistence.model.GroupEntity;
import be.ttime.core.persistence.model.RoleEntity;
import be.ttime.core.persistence.model.UserEntity;
import be.ttime.core.persistence.service.IAuthorityService;
import be.ttime.core.persistence.service.IUserService;
import be.ttime.core.util.CmsUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
@RequestMapping(value = "/admin/group")
@Slf4j
@PreAuthorize("hasRole('ROLE_ADMIN_GROUP')")
public class AdminGroupController {

    @Autowired
    private IAuthorityService authorityService;

    @Autowired
    private IUserService userService;

    @Autowired
    private SessionRegistry sessionRegistry;

    private final static String VIEWPATH = "admin/group/";


    @RequestMapping(value = "", method = RequestMethod.GET)
    public String home(ModelMap model) {
        return VIEWPATH + "home";
    }

    @RequestMapping(value = "/getJson", method = RequestMethod.GET)
    @ResponseBody
    public String getjson(HttpServletResponse response) {

        return authorityService.jsonAdminGroup();
    }

    @RequestMapping(value = "/edit", method = RequestMethod.GET)
    public String edit(ModelMap model, @ModelAttribute("redirectMessage") RedirectMessage redirectMessage) {
        return edit(null, model, redirectMessage);
    }

    @RequestMapping(value = "/edit/{id}", method = RequestMethod.GET)
    public String edit(@PathVariable("id") Long id, ModelMap model, @ModelAttribute("redirectMessage") RedirectMessage redirectMessage) {
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().disableHtmlEscaping().create();
        if (id != null) {
            GroupEntity group = authorityService.findGroupById(id);

            if (group == null) {
                throw new ResourceNotFoundException("Role with id \" + id + \" not found!\"");

            }

            model.put("group", group);
        }
        model.put("redirectMessage", redirectMessage);
        model.put("rolesByGroup", authorityService.getRoleByGroup());
        model.put("isSuperAdmin", CmsUtils.isSuperAdmin());
        return VIEWPATH + "edit";
    }

    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    public String edit(ModelMap model, Long id, String name, String description, Long[] roles, HttpServletRequest request, RedirectAttributes redirectAttributes) throws ResourceNotFoundException {
        GroupEntity role;
        if(id != null){
            role = authorityService.findGroupById(id);
            if(role == null){
                throw new ResourceNotFoundException("User with id \" + id + \" not found!\"");
            }
        } else{
            role = new GroupEntity();
            role.setDeletable(true);
        }

        boolean isSuperAdmin = CmsUtils.isSuperAdmin();

        Set<RoleEntity> roleEntityList = new HashSet<>();
        RoleEntity roleEntity;
        if(roles != null) {
            for (Long roleId : roles) {
                roleEntity = authorityService.findRoleById(roleId);
                if (roleEntity == null)
                    throw new IllegalArgumentException("role with id " + roleId + " is not found");
                if (!isSuperAdmin && roleEntity.isSuperAdmin())
                    continue;
                roleEntityList.add(roleEntity);
            }
        }
        role.setName(name);
        role.setDescription(description);
        role.setRoles(roleEntityList);
        role = authorityService.saveGroup(role);

        /* RELOAD THE RIGHT */
        List<Object> loggedUsers = sessionRegistry.getAllPrincipals();
        for (Object principal : loggedUsers) {
            if(principal != null && principal instanceof UserEntity) {
                final UserEntity loggedUser = (UserEntity) principal;
                UserEntity databaseUser = userService.findById(loggedUser.getId());
                CmsUtils.updateSessionUser(databaseUser);
            }
        }
        RedirectMessage redirectMessage = new RedirectMessage();
        redirectMessage.setType(RedirectMessage.SUCCESS);
        redirectMessage.setMessage("success.general");
        redirectAttributes.addFlashAttribute("redirectMessage", redirectMessage);

        return "redirect:/admin/group/edit/" + role.getId() ;
    }


    @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    public void delete(@PathVariable("id") Long id, HttpServletResponse response) {

        if (id == null) {
            response.setStatus(500);
        }
        try {
            GroupEntity group = authorityService.findGroupById(id);
            if(group == null || group.getName().equals("GROUP_SUPER_ADMIN")){
                response.setStatus(500);
            } else {
                if(group.getName().equals("GROUP_ADMIN") && !CmsUtils.isSuperAdmin()){
                    response.setStatus(500);
                } else{
                    authorityService.deleteGroup(id);
                }
            }

        } catch (Exception e) {
            response.setStatus(500);
        }
    }
}