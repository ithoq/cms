package be.ttime.core.persistence.model;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;
import com.mysema.query.types.path.PathInits;


/**
 * QContentDataDictionaryEntity is a Querydsl query type for ContentDataDictionaryEntity
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QContentDataDictionaryEntity extends EntityPathBase<ContentDataDictionaryEntity> {

    private static final long serialVersionUID = 880485905L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QContentDataDictionaryEntity contentDataDictionaryEntity = new QContentDataDictionaryEntity("contentDataDictionaryEntity");

    public final QContentDataEntity contentData;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath name = createString("name");

    public final StringPath type = createString("type");

    public final StringPath value = createString("value");

    public QContentDataDictionaryEntity(String variable) {
        this(ContentDataDictionaryEntity.class, forVariable(variable), INITS);
    }

    public QContentDataDictionaryEntity(Path<? extends ContentDataDictionaryEntity> path) {
        this(path.getType(), path.getMetadata(), path.getMetadata().isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QContentDataDictionaryEntity(PathMetadata<?> metadata) {
        this(metadata, metadata.isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QContentDataDictionaryEntity(PathMetadata<?> metadata, PathInits inits) {
        this(ContentDataDictionaryEntity.class, metadata, inits);
    }

    public QContentDataDictionaryEntity(Class<? extends ContentDataDictionaryEntity> type, PathMetadata<?> metadata, PathInits inits) {
        super(type, metadata, inits);
        this.contentData = inits.isInitialized("contentData") ? new QContentDataEntity(forProperty("contentData"), inits.get("contentData")) : null;
    }

}

