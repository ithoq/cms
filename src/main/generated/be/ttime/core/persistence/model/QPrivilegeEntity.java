package be.ttime.core.persistence.model;

import com.mysema.query.types.Path;
import com.mysema.query.types.PathMetadata;
import com.mysema.query.types.path.*;

import javax.annotation.Generated;

import static com.mysema.query.types.PathMetadataFactory.forVariable;


/**
 * QPrivilegeEntity is a Querydsl query type for PrivilegeEntity
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QPrivilegeEntity extends EntityPathBase<PrivilegeEntity> {

    private static final long serialVersionUID = 9769353L;

    public static final QPrivilegeEntity privilegeEntity = new QPrivilegeEntity("privilegeEntity");

    public final SetPath<FileEntity, QFileEntity> files = this.<FileEntity, QFileEntity>createSet("files", FileEntity.class, QFileEntity.class, PathInits.DIRECT2);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath name = createString("name");

    public final SetPath<PageEntity, QPageEntity> pages = this.<PageEntity, QPageEntity>createSet("pages", PageEntity.class, QPageEntity.class, PathInits.DIRECT2);

    public final CollectionPath<RoleEntity, QRoleEntity> roles = this.<RoleEntity, QRoleEntity>createCollection("roles", RoleEntity.class, QRoleEntity.class, PathInits.DIRECT2);

    public QPrivilegeEntity(String variable) {
        super(PrivilegeEntity.class, forVariable(variable));
    }

    public QPrivilegeEntity(Path<? extends PrivilegeEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QPrivilegeEntity(PathMetadata<?> metadata) {
        super(PrivilegeEntity.class, metadata);
    }

}

