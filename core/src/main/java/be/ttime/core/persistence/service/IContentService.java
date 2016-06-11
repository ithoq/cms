package be.ttime.core.persistence.service;

import be.ttime.core.persistence.model.ContentDataEntity;
import be.ttime.core.persistence.model.ContentEntity;

import java.util.List;
import java.util.Locale;

public interface IContentService {

    ContentEntity saveContent(ContentEntity p);

    List<ContentEntity> saveContent(List<ContentEntity> pages);

    String getNavMenu(String lang);

    String getPagesTree();

    void delete(Long id) throws Exception;

    ContentEntity findContentAdmin(Long id);

    ContentEntity findContentAndContentData(Long id, String locale);

    List<ContentDataEntity> saveContentData(List<ContentDataEntity> contents);

    ContentDataEntity saveContentData(ContentDataEntity content);

    ContentEntity findContent(Long id);

    ContentDataEntity findContentData(Long id);

    ContentDataEntity findBySlug(String slug, Locale locale);

}
