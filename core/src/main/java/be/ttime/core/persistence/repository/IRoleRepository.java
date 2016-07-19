package be.ttime.core.persistence.repository;


import be.ttime.core.persistence.model.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface IRoleRepository extends JpaRepository<RoleEntity, Long>, QueryDslPredicateExecutor<RoleEntity> {

    RoleEntity findByName(String name);

    @Override
    void delete(RoleEntity role);

    @Query("SELECT r from RoleEntity r  WHERE r.name <> :role ")
    List<RoleEntity> findAllForClient(@Param("role") String role);
}