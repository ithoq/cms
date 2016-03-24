package be.ttime.core.persistence.repository;

import be.ttime.core.persistence.dao.LocalizedMessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

public interface ILocalizedMessageRepository extends JpaRepository<LocalizedMessageEntity, Long>, QueryDslPredicateExecutor<LocalizedMessageEntity> {

    LocalizedMessageEntity findByMessageKey(String key);
}
