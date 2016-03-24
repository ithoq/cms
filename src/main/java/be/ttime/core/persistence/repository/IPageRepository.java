package be.ttime.core.persistence.repository;

import be.ttime.core.persistence.dao.PageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface IPageRepository extends JpaRepository<PageEntity, Long>, QueryDslPredicateExecutor<PageEntity> {

    @Query("SELECT p from PageEntity p LEFT JOIN FETCH p.pageChildren ORDER BY p.level,p.order")
    List<PageEntity> findAll();

    @Query("SELECT p from PageEntity p LEFT JOIN FETCH p.pageChildren WHERE p.menuItem = :nav AND p.enabled = :enabled ORDER BY p.level,p.order")
    List<PageEntity> findByMenuItemTrueAndEnabledTrue();

    List<PageEntity> findByPageParentIsNullOrderByOrderAsc();

    List<PageEntity> findByPageParentOrderByOrderAsc(PageEntity parent);

    PageEntity findFirstByPageParentOrderByOrderDesc(PageEntity parent);

    @Query("SELECT p from PageEntity p LEFT JOIN FETCH p.pageFiles WHERE p.slug = :slug ")
    PageEntity findBySlug(@Param("slug") String slug);

    @Query("SELECT p from PageEntity p LEFT JOIN FETCH p.pageFiles LEFT JOIN FETCH p.pageTemplate WHERE p.id = :id ")
    PageEntity findOne(@Param("id") Long id);

}

    /*
        @Query(value = "SELECT p FROM Place p LEFT JOIN FETCH p.author LEFT JOIN FETCH p.city c LEFT JOIN FETCH c.state where p.id = :id")
    Place findById(@Param("id") int id);

     */