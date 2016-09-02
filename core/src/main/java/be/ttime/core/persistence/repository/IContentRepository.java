package be.ttime.core.persistence.repository;

import be.ttime.core.persistence.model.ContentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface IContentRepository extends JpaRepository<ContentEntity, Long>, QueryDslPredicateExecutor<ContentEntity> {

    @Query("SELECT c from ContentEntity c LEFT JOIN FETCH c.contentChildren ORDER BY c.position")
    List<ContentEntity> findAllByContentTypeNameLike(String type);

    @Query("SELECT c from ContentEntity c LEFT JOIN FETCH c.contentChildren WHERE c.menuItem = :nav AND c.enabled = :enabled ORDER BY c.position")
    List<ContentEntity> findByMenuItemTrueAndEnabledTrue();

    List<ContentEntity> findByContentTypeNameLikeAndContentParentIsNullOrderByPositionAsc(String type);

    List<ContentEntity> findByContentParentOrderByPositionAsc(ContentEntity parent);

    @Query("SELECT DISTINCT c from ContentEntity c LEFT JOIN FETCH c.contentDataList d WHERE c.contentType.name = :type AND d.language.locale = :locale ORDER BY c.beginDate DESC")
    List<ContentEntity>findAllByContentTypeNameAndContentDataListLanguageLocale(@Param("type") String type, @Param("locale")String locale);

    @Query("SELECT DISTINCT c from ContentEntity c LEFT JOIN FETCH c.contentDataList WHERE c.contentType.name = :type ORDER BY c.beginDate DESC")
    List<ContentEntity>findAllByContentTypeName(@Param("type") String type);
    /*
    @Query("SELECT c from ContentEntity c LEFT JOIN FETCH c. WHERE c.menuItem = :nav AND c.enabled = :enabled ORDER BY c.position")
    ContentEntity findByComputedSlug(String slug, String locale);
*/
    ContentEntity findFirstByContentParentOrderByPositionDesc(ContentEntity parent);

    @Query("SELECT c from ContentEntity c LEFT JOIN FETCH c.contentDataList WHERE c.id = :id ")
    ContentEntity findOne(@Param("id") Long id);
}
