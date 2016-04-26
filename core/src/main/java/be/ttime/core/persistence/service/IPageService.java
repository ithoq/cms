package be.ttime.core.persistence.service;

import be.ttime.core.persistence.model.ContentEntity;
import be.ttime.core.persistence.model.PageEntity;

import java.util.List;
import java.util.Locale;

public interface IPageService {

    PageEntity find(Long id);

    //PageEntity findByParentIdIsNull();

    PageEntity savePage(PageEntity p);

    List<PageEntity> savePage(List<PageEntity> pages);

    List<PageEntity> getNavPages();

    String getPagesTree();

    void delete(Long id) throws Exception;

    PageEntity findWithChildren(Long id);

    List<ContentEntity> saveContents(List<ContentEntity> contents);

    ContentEntity saveContent(ContentEntity content);

    ContentEntity findContentById(Long id);

    ContentEntity findBySlug(String slug, Locale locale);

}
