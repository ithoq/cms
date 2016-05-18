package be.ttime.core.persistence.repository;


import be.ttime.core.persistence.model.ContentTemplateFieldsetEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

public interface IContentTemplateFieldsetRepository extends JpaRepository<ContentTemplateFieldsetEntity, Long>, QueryDslPredicateExecutor<ContentTemplateFieldsetEntity> {

}