package be.ttime.core.persistence.dao;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;


/**
 * QAuthorityEntity is a Querydsl query type for AuthorityEntity
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QAuthorityEntity extends EntityPathBase<AuthorityEntity> {

    private static final long serialVersionUID = -315598460L;

    public static final QAuthorityEntity authorityEntity = new QAuthorityEntity("authorityEntity");

    public final StringPath authority = createString("authority");

    public final SetPath<FileEntity, QFileEntity> files = this.<FileEntity, QFileEntity>createSet("files", FileEntity.class, QFileEntity.class, PathInits.DIRECT2);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final SetPath<PageEntity, QPageEntity> pages = this.<PageEntity, QPageEntity>createSet("pages", PageEntity.class, QPageEntity.class, PathInits.DIRECT2);

    public final SetPath<UserEntity, QUserEntity> users = this.<UserEntity, QUserEntity>createSet("users", UserEntity.class, QUserEntity.class, PathInits.DIRECT2);

    public QAuthorityEntity(String variable) {
        super(AuthorityEntity.class, forVariable(variable));
    }

    public QAuthorityEntity(Path<? extends AuthorityEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QAuthorityEntity(PathMetadata<?> metadata) {
        super(AuthorityEntity.class, metadata);
    }

}

