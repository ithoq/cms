package be.ttime.core.persistence.model;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;
import com.mysema.query.types.path.PathInits;


/**
 * QUserCustomEntity is a Querydsl query type for UserCustomEntity
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QUserCustomEntity extends EntityPathBase<UserCustomEntity> {

    private static final long serialVersionUID = -1712928470L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QUserCustomEntity userCustomEntity = new QUserCustomEntity("userCustomEntity");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath name = createString("name");

    public final StringPath type = createString("type");

    public final QUserEntity user;

    public final StringPath value = createString("value");

    public QUserCustomEntity(String variable) {
        this(UserCustomEntity.class, forVariable(variable), INITS);
    }

    public QUserCustomEntity(Path<? extends UserCustomEntity> path) {
        this(path.getType(), path.getMetadata(), path.getMetadata().isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QUserCustomEntity(PathMetadata<?> metadata) {
        this(metadata, metadata.isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QUserCustomEntity(PathMetadata<?> metadata, PathInits inits) {
        this(UserCustomEntity.class, metadata, inits);
    }

    public QUserCustomEntity(Class<? extends UserCustomEntity> type, PathMetadata<?> metadata, PathInits inits) {
        super(type, metadata, inits);
        this.user = inits.isInitialized("user") ? new QUserEntity(forProperty("user"), inits.get("user")) : null;
    }

}

