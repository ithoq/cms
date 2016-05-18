package be.ttime.core.persistence.repository;

import be.ttime.core.persistence.model.BlockEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

public interface IBlockRepository extends JpaRepository<BlockEntity, String>, QueryDslPredicateExecutor<BlockEntity> {

    BlockEntity findByNameAndBlockTypeName(String name, String blockType);
}