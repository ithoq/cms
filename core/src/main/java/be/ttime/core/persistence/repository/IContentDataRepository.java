package be.ttime.core.persistence.repository;

import be.ttime.core.persistence.model.ContentDataEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.query.Param;


public interface IContentDataRepository extends JpaRepository<ContentDataEntity, Long>, QueryDslPredicateExecutor<ContentDataEntity> {

    @Query("SELECT c from ContentDataEntity c LEFT JOIN FETCH c.contentFiles WHERE c.computedSlug = :slug AND c.language.locale = :id")
    ContentDataEntity findByComputedSlugAndLanguageLocale(@Param("slug") String slug, @Param("id")String id);

}
