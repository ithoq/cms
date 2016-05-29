package be.ttime.core.persistence.service;

import be.ttime.core.persistence.model.ContentDataEntity;
import be.ttime.core.persistence.model.ContentEntity;

import java.util.List;
import java.util.Locale;

public interface IContentService {

    ContentEntity find(Long id);

    //PageEntity findByParentIdIsNull();

    ContentEntity savePage(ContentEntity p);

    List<ContentEntity> savePage(List<ContentEntity> pages);

    List<ContentEntity> getNavPages();

    String getPagesTree();

    void delete(Long id) throws Exception;

    ContentEntity findWithChildren(Long id);

    List<ContentDataEntity> saveContents(List<ContentDataEntity> contents);

    ContentDataEntity saveContent(ContentDataEntity content);

    ContentDataEntity findContentById(Long id);

    ContentDataEntity findBySlug(String slug, Locale locale);

    ContentEntity findContentParent(Long id);

}
