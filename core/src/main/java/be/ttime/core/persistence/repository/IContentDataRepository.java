package be.ttime.core.persistence.repository;

import be.ttime.core.persistence.model.ContentDataEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;


public interface IContentDataRepository extends JpaRepository<ContentDataEntity, Long>, QueryDslPredicateExecutor<ContentDataEntity> {

    ContentDataEntity findByComputedSlugAndLanguageLocale(String slug, String id);
}
