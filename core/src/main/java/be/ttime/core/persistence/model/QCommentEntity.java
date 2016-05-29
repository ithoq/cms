package be.ttime.core.persistence.model;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;
import com.mysema.query.types.path.PathInits;


/**
 * QCommentEntity is a Querydsl query type for CommentEntity
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QCommentEntity extends EntityPathBase<CommentEntity> {

    private static final long serialVersionUID = 826339319L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QCommentEntity commentEntity = new QCommentEntity("commentEntity");

    public final BooleanPath approved = createBoolean("approved");

    public final StringPath author = createString("author");

    public final StringPath authorEmail = createString("authorEmail");

    public final StringPath authorIp = createString("authorIp");

    public final StringPath authorurl = createString("authorurl");

    public final ListPath<CommentEntity, QCommentEntity> children = this.<CommentEntity, QCommentEntity>createList("children", CommentEntity.class, QCommentEntity.class, PathInits.DIRECT2);

    public final StringPath content = createString("content");

    public final QContentDataEntity contentData;

    public final DatePath<java.util.Date> createdDate = createDate("createdDate", java.util.Date.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QCommentEntity parent;

    public QCommentEntity(String variable) {
        this(CommentEntity.class, forVariable(variable), INITS);
    }

    public QCommentEntity(Path<? extends CommentEntity> path) {
        this(path.getType(), path.getMetadata(), path.getMetadata().isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QCommentEntity(PathMetadata<?> metadata) {
        this(metadata, metadata.isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QCommentEntity(PathMetadata<?> metadata, PathInits inits) {
        this(CommentEntity.class, metadata, inits);
    }

    public QCommentEntity(Class<? extends CommentEntity> type, PathMetadata<?> metadata, PathInits inits) {
        super(type, metadata, inits);
        this.contentData = inits.isInitialized("contentData") ? new QContentDataEntity(forProperty("contentData"), inits.get("contentData")) : null;
        this.parent = inits.isInitialized("parent") ? new QCommentEntity(forProperty("parent"), inits.get("parent")) : null;
    }

}

