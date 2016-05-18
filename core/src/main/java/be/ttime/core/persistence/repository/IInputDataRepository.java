package be.ttime.core.persistence.repository;

import be.ttime.core.persistence.model.InputDataEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

public interface IInputDataRepository extends JpaRepository<InputDataEntity, Long>, QueryDslPredicateExecutor<InputDataEntity> {

}