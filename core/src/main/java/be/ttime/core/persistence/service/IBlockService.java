package be.ttime.core.persistence.service;

import be.ttime.core.persistence.model.BlockEntity;

import java.util.List;

public interface IBlockService {

    BlockEntity find(String name);

    List<BlockEntity> findAll();

    void delete(String name) throws Exception;

    BlockEntity save(BlockEntity block);

    List<BlockEntity> save(List<BlockEntity> blocks);

    String jsonBlockArray(String type);
}