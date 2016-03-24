package be.ttime.core.persistence.service;

import be.ttime.core.persistence.dao.PageBlockEntity;

import java.util.List;

public interface IPageBlockService {

    PageBlockEntity find(Long id);

    List<PageBlockEntity> findAll();

    void delete(Long id) throws Exception;

    String render(Long id) throws Exception;

    PageBlockEntity save(PageBlockEntity p);

    PageBlockEntity findByNameAndBlockType(String name, PageBlockEntity.BlockType type);

    String jsonBlockArray();
}