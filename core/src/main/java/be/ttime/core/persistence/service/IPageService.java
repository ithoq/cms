package be.ttime.core.persistence.service;

import be.ttime.core.persistence.model.PageContentEntity;
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

    List<PageContentEntity> saveContents(List<PageContentEntity> contents);

    PageContentEntity saveContent(PageContentEntity content);

    PageContentEntity findContentById(Long id);

    PageContentEntity findBySlug(String slug, Locale locale);

}
