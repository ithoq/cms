package be.ttime.core.persistence.model;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;
import com.mysema.query.types.path.PathInits;


/**
 * QVerificationTokenEntity is a Querydsl query type for VerificationTokenEntity
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QVerificationTokenEntity extends EntityPathBase<VerificationTokenEntity> {

    private static final long serialVersionUID = 1457740982L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QVerificationTokenEntity verificationTokenEntity = new QVerificationTokenEntity("verificationTokenEntity");

    public final DateTimePath<java.util.Date> expiryDate = createDateTime("expiryDate", java.util.Date.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath token = createString("token");

    public final QUserEntity user;

    public QVerificationTokenEntity(String variable) {
        this(VerificationTokenEntity.class, forVariable(variable), INITS);
    }

    public QVerificationTokenEntity(Path<? extends VerificationTokenEntity> path) {
        this(path.getType(), path.getMetadata(), path.getMetadata().isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QVerificationTokenEntity(PathMetadata<?> metadata) {
        this(metadata, metadata.isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QVerificationTokenEntity(PathMetadata<?> metadata, PathInits inits) {
        this(VerificationTokenEntity.class, metadata, inits);
    }

    public QVerificationTokenEntity(Class<? extends VerificationTokenEntity> type, PathMetadata<?> metadata, PathInits inits) {
        super(type, metadata, inits);
        this.user = inits.isInitialized("user") ? new QUserEntity(forProperty("user"), inits.get("user")) : null;
    }

}

