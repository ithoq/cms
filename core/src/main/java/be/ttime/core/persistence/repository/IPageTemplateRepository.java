package be.ttime.core.persistence.repository;

import be.ttime.core.persistence.model.PageTemplateEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

public interface IPageTemplateRepository extends JpaRepository<PageTemplateEntity, Long>, QueryDslPredicateExecutor<PageTemplateEntity> {
}