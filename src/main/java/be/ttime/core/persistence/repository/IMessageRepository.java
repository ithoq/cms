package be.ttime.core.persistence.repository;

import be.ttime.core.persistence.model.MessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

public interface IMessageRepository extends JpaRepository<MessageEntity, Long>, QueryDslPredicateExecutor<MessageEntity> {

    MessageEntity findByMessageKey(String key);
}
