package be.ttime.core.persistence.service;

import be.ttime.core.persistence.model.ContentDataEntity;
import be.ttime.core.persistence.model.ContentEntity;

import java.util.List;
import java.util.Locale;

public interface IContentService {

    ContentEntity find(Long id);

    ContentEntity savePage(ContentEntity p);

    List<ContentEntity> savePage(List<ContentEntity> pages);

    String getNavMenu(String lang);

    String getPagesTree();

    void delete(Long id) throws Exception;

    ContentEntity findContentAdmin(Long id);

    ContentEntity findContentData(Long id, String locale);

    List<ContentDataEntity> saveContents(List<ContentDataEntity> contents);

    ContentDataEntity saveContent(ContentDataEntity content);

    ContentDataEntity findContentById(Long id);

    ContentDataEntity findBySlug(String slug, Locale locale);

}
