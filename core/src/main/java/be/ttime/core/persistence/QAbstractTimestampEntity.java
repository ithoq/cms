package be.ttime.core.persistence;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;


/**
 * QAbstractTimestampEntity is a Querydsl query type for AbstractTimestampEntity
 */
@Generated("com.mysema.query.codegen.SupertypeSerializer")
public class QAbstractTimestampEntity extends EntityPathBase<AbstractTimestampEntity> {

    private static final long serialVersionUID = -508828175L;

    public static final QAbstractTimestampEntity abstractTimestampEntity = new QAbstractTimestampEntity("abstractTimestampEntity");

    public final DateTimePath<java.util.Date> created = createDateTime("created", java.util.Date.class);

    public final DateTimePath<java.util.Date> updated = createDateTime("updated", java.util.Date.class);

    public QAbstractTimestampEntity(String variable) {
        super(AbstractTimestampEntity.class, forVariable(variable));
    }

    public QAbstractTimestampEntity(Path<? extends AbstractTimestampEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QAbstractTimestampEntity(PathMetadata<?> metadata) {
        super(AbstractTimestampEntity.class, metadata);
    }

}

