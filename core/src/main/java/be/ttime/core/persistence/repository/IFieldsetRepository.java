package be.ttime.core.persistence.repository;

import be.ttime.core.persistence.model.FieldsetEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

public interface IFieldsetRepository extends JpaRepository<FieldsetEntity, Long>, QueryDslPredicateExecutor<FieldsetEntity> {

}