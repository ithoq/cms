package be.ttime.core.persistence.dao;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;

/**
 * QUserEntity is a Querydsl query type for UserEntity
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QUserEntity extends EntityPathBase<UserEntity> {

    private static final long serialVersionUID = -812490672L;

    public static final QUserEntity userEntity = new QUserEntity("userEntity");

    public final BooleanPath accountNonExpired = createBoolean("accountNonExpired");

    public final BooleanPath accountNonLocked = createBoolean("accountNonLocked");

    public final SetPath<AuthorityEntity, QAuthorityEntity> authorities = this.<AuthorityEntity, QAuthorityEntity>createSet("authorities", AuthorityEntity.class, QAuthorityEntity.class, PathInits.DIRECT2);

    public final BooleanPath credentialsNonExpired = createBoolean("credentialsNonExpired");

    public final BooleanPath enabled = createBoolean("enabled");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath password = createString("password");

    public final StringPath username = createString("username");

    public QUserEntity(String variable) {
        super(UserEntity.class, forVariable(variable));
    }

    public QUserEntity(Path<? extends UserEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QUserEntity(PathMetadata<?> metadata) {
        super(UserEntity.class, metadata);
    }

}

