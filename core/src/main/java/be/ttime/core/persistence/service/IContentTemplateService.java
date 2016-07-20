package be.ttime.core.persistence.service;

import be.ttime.core.persistence.model.ContentTemplateEntity;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

public interface IContentTemplateService {

    List<ContentTemplateEntity> findAllByTypeLike(String type);

    ContentTemplateEntity findByName(String name);

    ContentTemplateEntity find(Long id);
  /*
    ContentTemplateEntity findWithFieldsetAndData(Long id);*/

    @PreAuthorize("hasRole('ROLE_SUPER_ADMIN')")
    ContentTemplateEntity save(ContentTemplateEntity contentTemplate);

    String jsonContent();

    @PreAuthorize("hasRole('ROLE_SUPER_ADMIN')")
    void delete(Long id) throws Exception;
}
