package be.ttime.core.persistence.service;

import be.ttime.core.persistence.dao.PageTemplateEntity;

import java.util.List;

public interface IPageTemplateService {

    List<PageTemplateEntity> findAll();

    PageTemplateEntity find(Long id);

    PageTemplateEntity save(PageTemplateEntity pageTemplate);
}
