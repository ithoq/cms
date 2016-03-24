package be.ttime.core.persistence.dao;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;


/**
 * QLocalizedMessageEntity is a Querydsl query type for LocalizedMessageEntity
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QLocalizedMessageEntity extends EntityPathBase<LocalizedMessageEntity> {

    private static final long serialVersionUID = 1828483281L;

    public static final QLocalizedMessageEntity localizedMessageEntity = new QLocalizedMessageEntity("localizedMessageEntity");

    public final StringPath domain = createString("domain");

    public final StringPath en = createString("en");

    public final StringPath fr = createString("fr");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath messageKey = createString("messageKey");

    public QLocalizedMessageEntity(String variable) {
        super(LocalizedMessageEntity.class, forVariable(variable));
    }

    public QLocalizedMessageEntity(Path<? extends LocalizedMessageEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QLocalizedMessageEntity(PathMetadata<?> metadata) {
        super(LocalizedMessageEntity.class, metadata);
    }

}

