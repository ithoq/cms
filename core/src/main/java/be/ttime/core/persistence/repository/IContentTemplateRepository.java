package be.ttime.core.persistence.repository;

import be.ttime.core.persistence.model.ContentTemplateEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface IContentTemplateRepository extends JpaRepository<ContentTemplateEntity, Long>, QueryDslPredicateExecutor<ContentTemplateEntity> {

    @Query("SELECT c from ContentTemplateEntity c LEFT JOIN FETCH c.contentTemplateFieldset LEFT JOIN FETCH c.block WHERE c.id = :id ")
    ContentTemplateEntity findByIdWithFieldset(@Param("id") Long id);

    List<ContentTemplateEntity> findByContentTypeNameLike(String name);

    @Query("SELECT c from ContentTemplateEntity c LEFT JOIN FETCH c.contentTemplateFieldset LEFT JOIN FETCH c.block WHERE c.name = :name ")
    ContentTemplateEntity findFirstByName(@Param("name") String name);
}