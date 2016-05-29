package be.ttime.core.persistence.model;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;


/**
 * QUserAttemptsEntity is a Querydsl query type for UserAttemptsEntity
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QUserAttemptsEntity extends EntityPathBase<UserAttemptsEntity> {

    private static final long serialVersionUID = -158790721L;

    public static final QUserAttemptsEntity userAttemptsEntity = new QUserAttemptsEntity("userAttemptsEntity");

    public final NumberPath<Integer> attempts = createNumber("attempts", Integer.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final DatePath<java.util.Date> lastModified = createDate("lastModified", java.util.Date.class);

    public final StringPath username = createString("username");

    public QUserAttemptsEntity(String variable) {
        super(UserAttemptsEntity.class, forVariable(variable));
    }

    public QUserAttemptsEntity(Path<? extends UserAttemptsEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QUserAttemptsEntity(PathMetadata<?> metadata) {
        super(UserAttemptsEntity.class, metadata);
    }

}

