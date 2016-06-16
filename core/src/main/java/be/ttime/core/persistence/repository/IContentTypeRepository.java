package be.ttime.core.persistence.repository;


import be.ttime.core.persistence.model.ContentTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

public interface IContentTypeRepository extends JpaRepository<ContentTypeEntity, String>, QueryDslPredicateExecutor<ContentTypeEntity> {


}