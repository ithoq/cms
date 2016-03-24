package be.ttime.core.persistence.service;

import be.ttime.core.persistence.dao.PageEntity;

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
}