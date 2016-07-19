package be.ttime.core.persistence.repository;

import be.ttime.core.persistence.model.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

public interface IRoleRepository extends JpaRepository<RoleEntity, Long>, QueryDslPredicateExecutor<RoleEntity> {

    RoleEntity findByName(String name);

    @Override
    void delete(RoleEntity privilege);
}