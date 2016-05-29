package be.ttime.core.persistence.model;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;
import com.mysema.query.types.path.PathInits;


/**
 * QContentDictionaryEntity is a Querydsl query type for ContentDictionaryEntity
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QContentDictionaryEntity extends EntityPathBase<ContentDictionaryEntity> {

    private static final long serialVersionUID = -1964847801L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QContentDictionaryEntity contentDictionaryEntity = new QContentDictionaryEntity("contentDictionaryEntity");

    public final QContentEntity content;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath name = createString("name");

    public final StringPath type = createString("type");

    public final StringPath value = createString("value");

    public QContentDictionaryEntity(String variable) {
        this(ContentDictionaryEntity.class, forVariable(variable), INITS);
    }

    public QContentDictionaryEntity(Path<? extends ContentDictionaryEntity> path) {
        this(path.getType(), path.getMetadata(), path.getMetadata().isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QContentDictionaryEntity(PathMetadata<?> metadata) {
        this(metadata, metadata.isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QContentDictionaryEntity(PathMetadata<?> metadata, PathInits inits) {
        this(ContentDictionaryEntity.class, metadata, inits);
    }

    public QContentDictionaryEntity(Class<? extends ContentDictionaryEntity> type, PathMetadata<?> metadata, PathInits inits) {
        super(type, metadata, inits);
        this.content = inits.isInitialized("content") ? new QContentEntity(forProperty("content"), inits.get("content")) : null;
    }

}

