package be.ttime.core.persistence.repository;

import be.ttime.core.persistence.model.InputDefinitionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

public interface InputDefinitionRepository extends JpaRepository<InputDefinitionEntity, Long>, QueryDslPredicateExecutor<InputDefinitionEntity> {

}