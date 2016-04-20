package be.ttime.core.persistence.repository;

import be.ttime.core.persistence.model.PageContentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;


public interface IPageContentRepository extends JpaRepository<PageContentEntity, Long>, QueryDslPredicateExecutor<PageContentEntity> {

    PageContentEntity findByComputedSlugAndLanguageLocale(String slug, String id);
}
