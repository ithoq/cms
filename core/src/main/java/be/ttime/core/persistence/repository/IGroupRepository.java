package be.ttime.core.persistence.repository;


import be.ttime.core.persistence.model.GroupEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface IGroupRepository extends JpaRepository<GroupEntity, Long>, QueryDslPredicateExecutor<GroupEntity> {

    GroupEntity findByName(String name);

    @Override
    void delete(GroupEntity role);

    @Query("SELECT g from GroupEntity g  WHERE g.name <> :group ")
    List<GroupEntity> findAllForClient(@Param("group") String group);
}