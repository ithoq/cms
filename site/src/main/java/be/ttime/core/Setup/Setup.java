package be.ttime.core.setup;

import be.ttime.core.persistence.model.PageBlockEntity;
import be.ttime.core.persistence.service.IApplicationService;

import be.ttime.core.persistence.service.IPageBlockService;
import be.ttime.core.util.CmsUtils;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
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
    private IPageBlockService pageBlockService;

    private static final String INSTALLATION_SCRIPT = "setup/script.sql";

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    net.sf.ehcache.CacheManager cacheManager;

    @Override
    @Transactional
    public void onApplicationEvent(ContextRefreshedEvent event) {

        if(applicationService.getApplicationConfig() == null) {
            try {
                // Db basics
                CmsUtils.executeResourceFileScript(entityManager, INSTALLATION_SCRIPT);

                // base blocks
                List<PageBlockEntity> blocks = new ArrayList<>();
                blocks.add(new PageBlockEntity(1, "text", CmsUtils.getResourceFileContent("setup/field_text.twig"), true, false, false, true, PageBlockEntity.BlockType.FieldSet, null, null));
                blocks.add(new PageBlockEntity(2, "tinymce", CmsUtils.getResourceFileContent("setup/field_tinymce.twig"), true, false, false, true, PageBlockEntity.BlockType.FieldSet, null, null));
                blocks.add(new PageBlockEntity(3, "master", CmsUtils.getResourceFileContent("setup/master.twig"), true, false, false, true, PageBlockEntity.BlockType.System, null, null));
                blocks.add(new PageBlockEntity(4, "login", CmsUtils.getResourceFileContent("setup/login.twig"), true, false, true, true, PageBlockEntity.BlockType.System, null, null));
                pageBlockService.save(blocks);
                cacheManager.clearAll();
            } catch(Exception e){
                log.error("Error during db initialisation " + e.toString());
            }
        }
    }
}
