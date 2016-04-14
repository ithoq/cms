package be.ttime.core.persistence.repository;

import be.ttime.core.persistence.model.PersistentLoginsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

public interface IPersistentLoginsRepository extends JpaRepository<PersistentLoginsEntity, Long>, QueryDslPredicateExecutor<PersistentLoginsEntity> {
    PersistentLoginsEntity findOneBySeries(String series);

    Long deleteByUsername(String username);
}