package be.ttime.core.persistence.repository;

import be.ttime.core.persistence.model.PasswordResetTokenEntity;
import be.ttime.core.persistence.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

public interface IPasswordResetTokenRepository extends JpaRepository<PasswordResetTokenEntity, Long>, QueryDslPredicateExecutor<PasswordResetTokenEntity> {

    PasswordResetTokenEntity findByToken(String token);

    PasswordResetTokenEntity findByUser(UserEntity user);
}