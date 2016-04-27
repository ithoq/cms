package be.ttime.core.persistence.repository;

import be.ttime.core.persistence.model.ContentTemplateEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

public interface IContentTemplateRepository extends JpaRepository<ContentTemplateEntity, Long>, QueryDslPredicateExecutor<ContentTemplateEntity> {
}