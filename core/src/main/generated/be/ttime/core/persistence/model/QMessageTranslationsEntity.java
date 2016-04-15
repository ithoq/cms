package be.ttime.core.persistence.model;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;
import com.mysema.query.types.path.PathInits;


/**
 * QMessageTranslationsEntity is a Querydsl query type for MessageTranslationsEntity
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QMessageTranslationsEntity extends EntityPathBase<MessageTranslationsEntity> {

    private static final long serialVersionUID = -232667103L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QMessageTranslationsEntity messageTranslationsEntity = new QMessageTranslationsEntity("messageTranslationsEntity");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QApplicationLanguageEntity language;

    public final QMessageEntity message;

    public final StringPath value = createString("value");

    public QMessageTranslationsEntity(String variable) {
        this(MessageTranslationsEntity.class, forVariable(variable), INITS);
    }

    public QMessageTranslationsEntity(Path<? extends MessageTranslationsEntity> path) {
        this(path.getType(), path.getMetadata(), path.getMetadata().isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QMessageTranslationsEntity(PathMetadata<?> metadata) {
        this(metadata, metadata.isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QMessageTranslationsEntity(PathMetadata<?> metadata, PathInits inits) {
        this(MessageTranslationsEntity.class, metadata, inits);
    }

    public QMessageTranslationsEntity(Class<? extends MessageTranslationsEntity> type, PathMetadata<?> metadata, PathInits inits) {
        super(type, metadata, inits);
        this.language = inits.isInitialized("language") ? new QApplicationLanguageEntity(forProperty("language")) : null;
        this.message = inits.isInitialized("message") ? new QMessageEntity(forProperty("message")) : null;
    }

}

