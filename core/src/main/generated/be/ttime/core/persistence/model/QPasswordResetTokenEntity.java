package be.ttime.core.persistence.model;

import com.mysema.query.types.Path;
import com.mysema.query.types.PathMetadata;
import com.mysema.query.types.path.*;

import javax.annotation.Generated;

import static com.mysema.query.types.PathMetadataFactory.forVariable;


/**
 * QPasswordResetTokenEntity is a Querydsl query type for PasswordResetTokenEntity
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QPasswordResetTokenEntity extends EntityPathBase<PasswordResetTokenEntity> {

    private static final long serialVersionUID = 68332339L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QPasswordResetTokenEntity passwordResetTokenEntity = new QPasswordResetTokenEntity("passwordResetTokenEntity");

    public final DateTimePath<java.util.Date> expiryDate = createDateTime("expiryDate", java.util.Date.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath token = createString("token");

    public final QUserEntity user;

    public QPasswordResetTokenEntity(String variable) {
        this(PasswordResetTokenEntity.class, forVariable(variable), INITS);
    }

    public QPasswordResetTokenEntity(Path<? extends PasswordResetTokenEntity> path) {
        this(path.getType(), path.getMetadata(), path.getMetadata().isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QPasswordResetTokenEntity(PathMetadata<?> metadata) {
        this(metadata, metadata.isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QPasswordResetTokenEntity(PathMetadata<?> metadata, PathInits inits) {
        this(PasswordResetTokenEntity.class, metadata, inits);
    }

    public QPasswordResetTokenEntity(Class<? extends PasswordResetTokenEntity> type, PathMetadata<?> metadata, PathInits inits) {
        super(type, metadata, inits);
        this.user = inits.isInitialized("user") ? new QUserEntity(forProperty("user")) : null;
    }

}

