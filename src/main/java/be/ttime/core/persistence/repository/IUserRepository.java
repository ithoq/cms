package be.ttime.core.persistence.repository;

import be.ttime.core.persistence.dao.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.query.Param;

public interface IUserRepository extends JpaRepository<UserEntity, Long>, QueryDslPredicateExecutor<UserEntity> {
    @Query("SELECT u from UserEntity u LEFT JOIN FETCH u.authorities WHERE u.username = :username")
    UserEntity findByUsername(@Param("username") String username);
}