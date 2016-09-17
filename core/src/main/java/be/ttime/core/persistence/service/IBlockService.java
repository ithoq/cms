package be.ttime.core.persistence.service;

import be.ttime.core.persistence.model.BlockEntity;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

public interface IBlockService {

    BlockEntity find(String name);

    List<BlockEntity> findAll();

    @PreAuthorize("hasRole('ROLE_ADMIN_BLOCK_DELETE')")
    void delete(String name) throws Exception;

    @PreAuthorize("hasRole('ROLE_ADMIN_BLOCK')")
    BlockEntity save(BlockEntity block);

    @PreAuthorize("hasRole('ROLE_ADMIN_BLOCK')")
    List<BlockEntity> save(List<BlockEntity> blocks);

    String jsonBlockArray(String type, boolean canDelete);
}
