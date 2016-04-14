package be.ttime.core.persistence.repository;

import be.ttime.core.persistence.model.UserAttemptsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.query.Param;

public interface IUsersAttemptsRepository extends JpaRepository<UserAttemptsEntity, Long>, QueryDslPredicateExecutor<UserAttemptsEntity> {
    UserAttemptsEntity findByUsername(String username);

    @Modifying
    @Query("update UserAttemptsEntity u set u.attempts = 0, u.lastModified = null where u.username = :username")
    void resetAttempts(@Param("username")String username);
}
