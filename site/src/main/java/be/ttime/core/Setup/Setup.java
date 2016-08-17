package be.ttime.core.setup;

import be.ttime.core.persistence.model.BlockEntity;
import be.ttime.core.persistence.model.BlockTypeEntity;
import be.ttime.core.persistence.model.RoleEntity;
import be.ttime.core.persistence.repository.IBlockRepository;
import be.ttime.core.persistence.service.IApplicationService;
import be.ttime.core.persistence.service.IAuthorityService;
import be.ttime.core.util.CmsUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;


@Component
@Slf4j
public class Setup implements ApplicationListener<ContextRefreshedEvent> {

    @Autowired
    private IApplicationService applicationService;

    @Autowired
    private IBlockRepository repository;

    @Autowired
    private IAuthorityService authorityService;

    private static final String INSTALLATION_SCRIPT = "setup/script.sql";

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private net.sf.ehcache.CacheManager cacheManager;

    @Override
    @Transactional
    public void onApplicationEvent(ContextRefreshedEvent event) {

        if(applicationService.getApplicationConfig() == null) {
            try {

                // Db basics
                CmsUtils.executeResourceFileScript(entityManager, INSTALLATION_SCRIPT);


                // base blocks
                List<BlockEntity> blocks = new ArrayList<>();
                blocks.add(new BlockEntity(CmsUtils.BLOCK_PAGE_MASTER, "master", CmsUtils.getResourceFileContent("setup/master.twig"), true, false, true, new BlockTypeEntity(CmsUtils.BLOCK_TYPE_SYSTEM), null));
                blocks.add(new BlockEntity(CmsUtils.BLOCK_PAGE_LOGIN, "login", CmsUtils.getResourceFileContent("setup/login.twig"), true, false, true, new BlockTypeEntity(CmsUtils.BLOCK_TYPE_SYSTEM), null));

                BlockEntity fieldText =  repository.findOne(CmsUtils.BLOCK_FIELD_TEXT);
                fieldText.setContent(CmsUtils.getResourceFileContent("setup/field_text.twig"));
                blocks.add(fieldText);

                BlockEntity fieldTextarea =  repository.findOne(CmsUtils.BLOCK_FIELD_TEXTAREA);
                fieldTextarea.setContent(CmsUtils.getResourceFileContent("setup/field_textarea.twig"));
                blocks.add(fieldTextarea);

                BlockEntity fieldTiny =  repository.findOne(CmsUtils.BLOCK_FIELD_TINYMCE);
                fieldTiny.setContent(CmsUtils.getResourceFileContent("setup/field_tinymce.twig"));
                blocks.add(fieldTiny);

                BlockEntity fieldDate =  repository.findOne(CmsUtils.BLOCK_FIELD_DATEPICKER);
                fieldDate.setContent(CmsUtils.getResourceFileContent("setup/field_datepicker.twig"));
                blocks.add(fieldDate);

                BlockEntity simplePage =  repository.findOne(CmsUtils.BLOCK_TEMPLATE_BASIC_PAGE);
                simplePage.setContent(CmsUtils.getResourceFileContent("setup/tpl_basic_page.twig"));
                blocks.add(simplePage);

                BlockEntity webcontentPage =  repository.findOne(CmsUtils.BLOCK_TEMPLATE_WEBCONTENT);
                webcontentPage.setContent(CmsUtils.getResourceFileContent("setup/tpl_webcontent.twig"));
                blocks.add(webcontentPage);

                repository.save(blocks);

                cacheManager.clearAll();

            } catch(Exception e){
                log.error("Error during db initialisation ", e);
            }
        }

        // load all privilege
        final List<GrantedAuthority> authorities = new ArrayList<>();
        for (RoleEntity roleEntity : authorityService.findAllRole()) {
            authorities.add(new SimpleGrantedAuthority(roleEntity.getName()));
        }
        CmsUtils.setFullPrivilegeList(authorities);

    }
}
