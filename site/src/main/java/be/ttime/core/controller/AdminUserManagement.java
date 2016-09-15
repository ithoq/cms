package be.ttime.core.controller;

import be.ttime.core.error.ResourceNotFoundException;
import be.ttime.core.model.RedirectMessage;
import be.ttime.core.model.form.AdminEditUser;
import be.ttime.core.persistence.model.GroupEntity;
import be.ttime.core.persistence.model.UserEntity;
import be.ttime.core.persistence.service.IApplicationService;
import be.ttime.core.persistence.service.IAuthorityService;
import be.ttime.core.persistence.service.IUserService;
import be.ttime.core.util.CmsUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
@RequestMapping(value = "/admin/user")
@Slf4j
@PreAuthorize("hasRole('ROLE_ADMIN_USER')")
public class AdminUserManagement {


    private final static String VIEWPATH = "admin/user/";
    @Autowired
    private IUserService userService;
    @Autowired
    private IApplicationService applicationService;
    @Autowired
    private IAuthorityService authorityService;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @RequestMapping(value= "", method = RequestMethod.GET)
    public String home(ModelMap model, @ModelAttribute("redirectMessage") RedirectMessage redirectMessage) {
        model.put("redirectMessage", redirectMessage);
        return VIEWPATH + "home";
    }


    @RequestMapping(value = "/getJson", method = RequestMethod.GET)
    @ResponseBody
    public String getjson(HttpServletResponse response) {

        return userService.jsonAdminContent();
    }

    @RequestMapping(value= "/edit", method = RequestMethod.POST)
    public String edit(ModelMap model, @Valid AdminEditUser form,  BindingResult result, RedirectAttributes redirectAttributes) throws ResourceNotFoundException, IOException {
        UserEntity userEntity;

        //UserEntity currentUser = CmsUtils.getCurrentUser();
        RedirectMessage redirectMessage = new RedirectMessage();
        if (result.hasErrors()) {
            redirectMessage.setType(RedirectMessage.ERROR);
            redirectMessage.setMessage("error.validation.message");
            redirectAttributes.addFlashAttribute("redirectMessage", redirectMessage);
            long id = 0;
            if(form.getId() != null){
                id = form.getId();
            }
            return "redirect:/admin/user/edit/" + id;
        } else {

            if (form.getId() == null) {
                userEntity = new UserEntity();
                userEntity.setPassword(bCryptPasswordEncoder.encode(form.getPassword()));
                userEntity.setEnabled(true);
                userEntity.setAccountNonExpired(true);
                userEntity.setAccountNonLocked(true);
                userEntity.setCredentialsNonExpired(true);

            } else {
                userEntity = userService.findById(form.getId());
                if (userEntity == null) {
                    throw new ResourceNotFoundException("User with id " + form.getId() + " not found!");
                }

                if(!StringUtils.isEmpty(form.getPassword())){
                    userEntity.setPassword(bCryptPasswordEncoder.encode(form.getPassword()));
                }
            }

            if(form.getAvatar() != null){
                // new or replace
                if( !form.getAvatar().isEmpty()){
                    File uploadedFile = CmsUtils.uploadFile(form.getAvatar(), false, "admin/avatar/" + userEntity.getId());
                    String resultPath = CmsUtils.getFilePath(uploadedFile, "public") + "/" + uploadedFile.getName();
                    userEntity.setAvatar(resultPath);
                }
            }

            userEntity.setLastName(form.getLastName());
            userEntity.setFirstName(form.getFirstName());
            userEntity.setBirthday(form.getBirthday());
            userEntity.setCity(form.getCity());
            userEntity.setComment(form.getComment());
            userEntity.setCountryName(CmsUtils.capitalizeFirstLetter(form.getCountry()));
            userEntity.setEmail(form.getEmail());
            userEntity.setGender(form.getGender());
            userEntity.setStreet1(form.getStreet());
            userEntity.setZip(form.getZip());
            userEntity.setEnabled(form.isEnabled());
            userEntity.setOrganisation(form.getOrganisation());

            if(!StringUtils.isEmpty(form.getGroup())) {
                Set<GroupEntity> roles = new HashSet<>();
                String[] rolesString = form.getGroup().split(",");
                for (String r : rolesString) {
                    if(r.equals(CmsUtils.GROUP_SUPER_ADMIN)){
                        if(!CmsUtils.isSuperAdmin()){
                            continue;
                        }
                    }
                    GroupEntity roleEntity = authorityService.findGroupByName(r);
                    roles.add(roleEntity);
                }
                userEntity.setGroups(roles);
            }
            userEntity = userService.save(userEntity);

            redirectMessage.setType(RedirectMessage.SUCCESS);
            redirectMessage.setMessage("success.general");
            redirectAttributes.addFlashAttribute("redirectMessage", redirectMessage);
            return "redirect:/admin/user/edit/" + userEntity.getId() ;
        }



    }

    @RequestMapping(value= "/edit", method = RequestMethod.GET)
    public String edit(ModelMap model,  RedirectAttributes redirectAttributes, @ModelAttribute("redirectMessage") RedirectMessage redirectMessage) {
        return edit(null, model, redirectAttributes, redirectMessage);
    }

    @RequestMapping(value= "/edit/{id}", method = RequestMethod.GET)
    public String edit(@PathVariable("id") Long id, ModelMap model,  RedirectAttributes redirectAttributes, @ModelAttribute("redirectMessage") RedirectMessage redirectMessage) {
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().disableHtmlEscaping().create();
        if(id != null){
            UserEntity user = userService.findById(id);

            if(user == null){
                throw new ResourceNotFoundException("User with id \" + id + \" not found!\"");
            }

            boolean role_super_admin = user.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_SUPER_ADMIN"));

            /*outerloop:
            for (GrantedAuthority authority : authorities) {
                if(authority.getAuthority().equals("ROLE_SUPER_ADMIN")) {
                    role_super_admin = true;
                    break outerloop;
                }
            }*/

            if(role_super_admin && !CmsUtils.isSuperAdmin()){
                RedirectMessage superAdminRedirectMessage = new RedirectMessage();
                superAdminRedirectMessage.setType(RedirectMessage.ERROR);
                superAdminRedirectMessage.setMessage("admin.editSuperAdmin.message");
                redirectAttributes.addFlashAttribute("redirectMessage", superAdminRedirectMessage);
                return "redirect:/admin/user";
            }

            if(user.getBirthday() != null){
                SimpleDateFormat sd = new SimpleDateFormat(CmsUtils.DATETIME_FORMAT);
                String dateBirthday= sd.format(user.getBirthday());
                String[] date = dateBirthday.split(" ");
                model.put("_birthday", date[0]);
            }

            model.put("editUser", user);
            List<String> userRoles = new ArrayList<>();
            for (GroupEntity roleEntity : user.getGroups()) {
                userRoles.add(roleEntity.getName());
            }
            /*model.put("userRoles", gson.toJson(userRoles));*/

        }

        model.put("redirectMessage", redirectMessage);
        model.put("groupList", authorityService.findAllClientGroup());
        return VIEWPATH + "edit";
    }

    @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    public void delete(@PathVariable("id") Long id, HttpServletResponse response) {

        if (id == null) {
            response.setStatus(500);
        }
        try {
            UserEntity user = userService.findById(id);
            boolean role_super_admin = user.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_SUPER_ADMIN"));

            if(role_super_admin && !CmsUtils.isSuperAdmin()){
                response.setStatus(500);
            } else {
                userService.delete(user);
            }

        } catch (Exception e) {
            response.setStatus(500);
        }
    }
}
