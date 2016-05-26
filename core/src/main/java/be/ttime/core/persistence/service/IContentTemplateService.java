package be.ttime.core.persistence.service;

import be.ttime.core.persistence.model.ContentTemplateEntity;

import java.util.List;

public interface IContentTemplateService {

    List<ContentTemplateEntity> findAllByTypeLike(String type);

    ContentTemplateEntity find(Long id);

    ContentTemplateEntity findWithFieldsetAndData(Long id);

    ContentTemplateEntity save(ContentTemplateEntity contentTemplate);

    String jsonContent();

    void delete(Long id) throws Exception;
}
