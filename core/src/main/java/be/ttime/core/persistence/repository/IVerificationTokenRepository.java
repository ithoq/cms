package be.ttime.core.persistence.repository;

import be.ttime.core.persistence.model.UserEntity;
import be.ttime.core.persistence.model.VerificationTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

public interface IVerificationTokenRepository extends JpaRepository<VerificationTokenEntity, Long>, QueryDslPredicateExecutor<VerificationTokenEntity> {

    VerificationTokenEntity findByToken(String token);

    VerificationTokenEntity findByUser(UserEntity user);
}