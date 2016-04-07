package be.ttime.core.persistence.repository;

import be.ttime.core.persistence.model.ApplicationConfigEntity;
import be.ttime.core.persistence.model.FileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

public interface IApplicationConfigRepository extends JpaRepository<ApplicationConfigEntity, Long>, QueryDslPredicateExecutor<ApplicationConfigEntity> {

}
