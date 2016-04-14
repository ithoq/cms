package be.ttime.core.persistence.model;

import com.mysema.query.types.Path;
import com.mysema.query.types.PathMetadata;
import com.mysema.query.types.path.*;

import javax.annotation.Generated;

import static com.mysema.query.types.PathMetadataFactory.forVariable;


/**
 * QRoleEntity is a Querydsl query type for RoleEntity
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QRoleEntity extends EntityPathBase<RoleEntity> {

    private static final long serialVersionUID = -292281532L;

    public static final QRoleEntity roleEntity = new QRoleEntity("roleEntity");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath name = createString("name");

    public final CollectionPath<PrivilegeEntity, QPrivilegeEntity> privileges = this.<PrivilegeEntity, QPrivilegeEntity>createCollection("privileges", PrivilegeEntity.class, QPrivilegeEntity.class, PathInits.DIRECT2);

    public final SetPath<UserEntity, QUserEntity> users = this.<UserEntity, QUserEntity>createSet("users", UserEntity.class, QUserEntity.class, PathInits.DIRECT2);

    public QRoleEntity(String variable) {
        super(RoleEntity.class, forVariable(variable));
    }

    public QRoleEntity(Path<? extends RoleEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QRoleEntity(PathMetadata<?> metadata) {
        super(RoleEntity.class, metadata);
    }

}

