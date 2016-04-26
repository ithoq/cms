package be.ttime.core.persistence.repository;

import be.ttime.core.persistence.model.ContentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;


public interface IPageContentRepository extends JpaRepository<ContentEntity, Long>, QueryDslPredicateExecutor<ContentEntity> {

    ContentEntity findByComputedSlugAndLanguageLocale(String slug, String id);
}
