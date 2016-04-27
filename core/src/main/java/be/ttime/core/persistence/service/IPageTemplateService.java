package be.ttime.core.persistence.service;

import be.ttime.core.persistence.model.ContentTemplateEntity;

import java.util.List;

public interface IPageTemplateService {

    List<ContentTemplateEntity> findAll();

    ContentTemplateEntity find(Long id);

    ContentTemplateEntity save(ContentTemplateEntity contentTemplate);
}
