package be.ttime.core.persistence.repository;

import be.ttime.core.persistence.model.BlockEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

import java.util.List;

public interface IBlockRepository extends JpaRepository<BlockEntity, String>, QueryDslPredicateExecutor<BlockEntity> {

    List<BlockEntity> findAllByBlockTypeName(String type);
}