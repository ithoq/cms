package be.ttime.core.persistence.model;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;
import com.mysema.query.types.path.PathInits;


/**
 * QContentDataEntity is a Querydsl query type for ContentDataEntity
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QContentDataEntity extends EntityPathBase<ContentDataEntity> {

    private static final long serialVersionUID = -634869797L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QContentDataEntity contentDataEntity = new QContentDataEntity("contentDataEntity");

    public final ListPath<CommentEntity, QCommentEntity> commentList = this.<CommentEntity, QCommentEntity>createList("commentList", CommentEntity.class, QCommentEntity.class, PathInits.DIRECT2);

    public final StringPath computedSlug = createString("computedSlug");

    public final QContentEntity content;

    public final ListPath<FileEntity, QFileEntity> contentFiles = this.<FileEntity, QFileEntity>createList("contentFiles", FileEntity.class, QFileEntity.class, PathInits.DIRECT2);

    public final ListPath<FileEntity, QFileEntity> ContentImages = this.<FileEntity, QFileEntity>createList("ContentImages", FileEntity.class, QFileEntity.class, PathInits.DIRECT2);

    public final DatePath<java.util.Date> createdDate = createDate("createdDate", java.util.Date.class);

    public final StringPath data = createString("data");

    public final ListPath<ContentDataDictionaryEntity, QContentDataDictionaryEntity> dictionaryList = this.<ContentDataDictionaryEntity, QContentDataDictionaryEntity>createList("dictionaryList", ContentDataDictionaryEntity.class, QContentDataDictionaryEntity.class, PathInits.DIRECT2);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QApplicationLanguageEntity language;

    public final DatePath<java.util.Date> modifiedDate = createDate("modifiedDate", java.util.Date.class);

    public final NumberPath<Integer> position = createNumber("position", Integer.class);

    public final StringPath slug = createString("slug");

    public final StringPath title = createString("title");

    public final NumberPath<Long> version = createNumber("version", Long.class);

    public QContentDataEntity(String variable) {
        this(ContentDataEntity.class, forVariable(variable), INITS);
    }

    public QContentDataEntity(Path<? extends ContentDataEntity> path) {
        this(path.getType(), path.getMetadata(), path.getMetadata().isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QContentDataEntity(PathMetadata<?> metadata) {
        this(metadata, metadata.isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QContentDataEntity(PathMetadata<?> metadata, PathInits inits) {
        this(ContentDataEntity.class, metadata, inits);
    }

    public QContentDataEntity(Class<? extends ContentDataEntity> type, PathMetadata<?> metadata, PathInits inits) {
        super(type, metadata, inits);
        this.content = inits.isInitialized("content") ? new QContentEntity(forProperty("content"), inits.get("content")) : null;
        this.language = inits.isInitialized("language") ? new QApplicationLanguageEntity(forProperty("language")) : null;
    }

}

