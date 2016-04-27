package be.ttime.core.persistence.repository;

import be.ttime.core.persistence.model.TagEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

public interface ITagRepository  extends JpaRepository<TagEntity, String>, QueryDslPredicateExecutor<TagEntity> {
}
