package be.ttime.core.persistence.repository;

import be.ttime.core.persistence.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface IUserRepository extends JpaRepository<UserEntity, Long>, QueryDslPredicateExecutor<UserEntity> {

    List<UserEntity> findByOrderByEnabledDescLastNameAsc();

    List<UserEntity> findByEnabledTrueAndAccountNonExpiredTrueAndAccountNonLockedTrueOrderByLastName();

    UserEntity findByUsername(String username);

    //UserEntity findByUsernameOrEmail(String username);

    Long countByUsername(String username);

    @Modifying
    @Query("update UserEntity u set u.accountNonLocked = :isNonLocked where u.username = :username")
    void updateAccountLocked(@Param("isNonLocked") boolean isNonLocked, @Param("username") String username);

    UserEntity findByEmail(String email);

    UserEntity findByEmailOrUsername(String email, String username);

}