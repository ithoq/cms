package be.ttime.core.persistence.service;

import be.ttime.core.persistence.model.BlockEntity;

import java.util.List;

public interface IBlockService {

    BlockEntity find(Long id);

    List<BlockEntity> findAll();

    void delete(Long id) throws Exception;

    String render(Long id) throws Exception;

    BlockEntity save(BlockEntity block);

    List<BlockEntity> save(List<BlockEntity> blocks);

    BlockEntity findByNameAndBlockType(String name, String blockTypeName);

    String jsonBlockArray();
}