package be.ttime.core.persistence.repository;

import be.ttime.core.persistence.model.BlockTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

public interface IBlockTypeRepository  extends JpaRepository<BlockTypeEntity, String>, QueryDslPredicateExecutor<BlockTypeEntity> {
}
