package be.ttime.core.persistence.repository;

import be.ttime.core.persistence.model.ContentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface IContentRepository extends JpaRepository<ContentEntity, Long>, QueryDslPredicateExecutor<ContentEntity> {

    @Query("SELECT p from PageEntity p LEFT JOIN FETCH p.pageChildren ORDER BY p.level,p.order")
    List<ContentEntity> findAll();

    @Query("SELECT p from PageEntity p LEFT JOIN FETCH p.pageChildren WHERE p.menuItem = :nav AND p.enabled = :enabled ORDER BY p.level,p.order")
    List<ContentEntity> findByMenuItemTrueAndEnabledTrue();

    List<ContentEntity> findByPageParentIsNullOrderByOrderAsc();

    List<ContentEntity> findByPageParentOrderByOrderAsc(ContentEntity parent);

    ContentEntity findFirstByPageParentOrderByOrderDesc(ContentEntity parent);

    @Query("SELECT p from PageEntity p LEFT JOIN FETCH p.pageContents WHERE p.id = :id ")
    ContentEntity findOne(@Param("id") Long id);
}
