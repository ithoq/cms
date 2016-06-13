package be.ttime.core.persistence.repository;

import be.ttime.core.persistence.model.ContentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface IContentRepository extends JpaRepository<ContentEntity, Long>, QueryDslPredicateExecutor<ContentEntity> {

    @Query("SELECT c from ContentEntity c LEFT JOIN FETCH c.contentChildren ORDER BY c.order")
    List<ContentEntity> findAll();

    @Query("SELECT c from ContentEntity c LEFT JOIN FETCH c.contentChildren WHERE c.menuItem = :nav AND c.enabled = :enabled ORDER BY c.order")
    List<ContentEntity> findByMenuItemTrueAndEnabledTrue();

    List<ContentEntity> findByContentParentIsNullOrderByOrderAsc();

    List<ContentEntity> findByContentParentOrderByOrderAsc(ContentEntity parent);

    List<ContentEntity>findAllByContentTypeNameAndDataListLanguageLocale(String Name, String locale);
    List<ContentEntity>findAllByContentTypeName(String Name);
    /*
    @Query("SELECT c from ContentEntity c LEFT JOIN FETCH c. WHERE c.menuItem = :nav AND c.enabled = :enabled ORDER BY c.order")
    ContentEntity findByComputedSlug(String slug, String locale);
*/
    ContentEntity findFirstByContentParentOrderByOrderDesc(ContentEntity parent);

    @Query("SELECT c from ContentEntity c LEFT JOIN FETCH c.dataList WHERE c.id = :id ")
    ContentEntity findOne(@Param("id") Long id);
}
