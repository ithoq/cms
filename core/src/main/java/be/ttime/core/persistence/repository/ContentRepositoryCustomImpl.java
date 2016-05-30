package be.ttime.core.persistence.repository;

import be.ttime.core.persistence.model.*;
import com.mysema.query.jpa.impl.JPAQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
public class ContentRepositoryCustomImpl implements IContentRepositoryCustom {

    @Autowired
    private EntityManager entityManager;

    @Override
    public ContentDataEntity findContentData(String slug, String locale, List<String> fetch) {
        QContentEntity contentEntity = QContentEntity.contentEntity;
        QContentDataEntity contentDataEntity = QContentDataEntity.contentDataEntity;
        QCommentEntity commentEntity = QCommentEntity.commentEntity;
        QFileEntity fileEntity = QFileEntity.fileEntity;
        QContentDataDictionaryEntity contentDataDictionaryEntity = QContentDataDictionaryEntity.contentDataDictionaryEntity;

        JPAQuery query = new JPAQuery(entityManager);

        ContentDataEntity resultData =  query.from(contentDataEntity)
                .leftJoin(contentDataEntity.commentList, commentEntity).fetch()
                .leftJoin(commentEntity.parent, commentEntity).fetch()
                .leftJoin(contentDataEntity.dictionaryList, contentDataDictionaryEntity).fetch()
                .leftJoin(contentDataEntity.contentFiles, fileEntity).fetch()
                .where(contentDataEntity.computedSlug.eq(slug).and(contentDataEntity.language.locale.eq(locale.toString())))
                .singleResult(contentDataEntity);

        return resultData;
    }

    @Override
    public ContentEntity findContent(Long id, List<String> festch) {
        QContentEntity contentEntity = QContentEntity.contentEntity;
        JPAQuery query = new JPAQuery(entityManager);
        ContentEntity result = query.from(contentEntity)
                .where(contentEntity.id.eq(id))
                .leftJoin(contentEntity.privileges).fetch()
                .leftJoin(contentEntity.contentParent)
                .singleResult(contentEntity);
        return result;
    }
}
