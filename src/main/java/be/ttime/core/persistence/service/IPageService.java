package be.ttime.core.persistence.service;

import be.ttime.core.persistence.model.PageContentEntity;
import be.ttime.core.persistence.model.PageEntity;
import sun.jvm.hotspot.debugger.Page;

import java.util.List;

public interface IPageService {

    PageEntity findBySlug(String slug);

    PageEntity find(Long id);

    //PageEntity findByParentIdIsNull();

    PageEntity save(PageEntity p);

    List<PageEntity> save(List<PageEntity> pages);

    List<PageEntity> getNavPages();

    String getPagesTree();

    void delete(Long id) throws Exception;

    PageEntity findWithChildren(Long id);

    List<PageContentEntity> saveContents(List<PageContentEntity> contents);

    PageContentEntity saveContent(PageContentEntity content);

    PageContentEntity findContentById(Long id);

}