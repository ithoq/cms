package be.ttime.core.persistence.repository;

import be.ttime.core.persistence.dao.PageBlockEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

public interface IPageBlockRepository extends JpaRepository<PageBlockEntity, Long>, QueryDslPredicateExecutor<PageBlockEntity> {

    PageBlockEntity findByName(String name);

    PageBlockEntity findByNameAndBlockType(String name, PageBlockEntity.BlockType type);
}