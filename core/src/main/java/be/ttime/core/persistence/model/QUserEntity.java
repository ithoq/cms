package be.ttime.core.persistence.model;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;
import com.mysema.query.types.path.PathInits;


/**
 * QUserEntity is a Querydsl query type for UserEntity
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QUserEntity extends EntityPathBase<UserEntity> {

    private static final long serialVersionUID = -183829799L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QUserEntity userEntity = new QUserEntity("userEntity");

    public final BooleanPath accountNonExpired = createBoolean("accountNonExpired");

    public final BooleanPath accountNonLocked = createBoolean("accountNonLocked");

    public final DatePath<java.util.Date> birthday = createDate("birthday", java.util.Date.class);

    public final StringPath city = createString("city");

    public final StringPath comment = createString("comment");

    public final QCompanyEntity company;

    public final StringPath countryName = createString("countryName");

    public final StringPath countyCode = createString("countyCode");

    public final DatePath<java.util.Date> createdDate = createDate("createdDate", java.util.Date.class);

    public final BooleanPath credentialsNonExpired = createBoolean("credentialsNonExpired");

    public final ListPath<UserCustomEntity, QUserCustomEntity> dictionaryList = this.<UserCustomEntity, QUserCustomEntity>createList("dictionaryList", UserCustomEntity.class, QUserCustomEntity.class, PathInits.DIRECT2);

    public final StringPath email = createString("email");

    public final BooleanPath enabled = createBoolean("enabled");

    public final StringPath firstName = createString("firstName");

    public final EnumPath<UserEntity.Gender> gender = createEnum("gender", UserEntity.Gender.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath lastName = createString("lastName");

    public final StringPath password = createString("password");

    public final DatePath<java.util.Date> passwordModifiedDate = createDate("passwordModifiedDate", java.util.Date.class);

    public final CollectionPath<RoleEntity, QRoleEntity> roles = this.<RoleEntity, QRoleEntity>createCollection("roles", RoleEntity.class, QRoleEntity.class, PathInits.DIRECT2);

    public final StringPath street1 = createString("street1");

    public final StringPath street2 = createString("street2");

    public final StringPath street3 = createString("street3");

    public final StringPath username = createString("username");

    public final StringPath userTitle = createString("userTitle");

    public final StringPath zip = createString("zip");

    public QUserEntity(String variable) {
        this(UserEntity.class, forVariable(variable), INITS);
    }

    public QUserEntity(Path<? extends UserEntity> path) {
        this(path.getType(), path.getMetadata(), path.getMetadata().isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QUserEntity(PathMetadata<?> metadata) {
        this(metadata, metadata.isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QUserEntity(PathMetadata<?> metadata, PathInits inits) {
        this(UserEntity.class, metadata, inits);
    }

    public QUserEntity(Class<? extends UserEntity> type, PathMetadata<?> metadata, PathInits inits) {
        super(type, metadata, inits);
        this.company = inits.isInitialized("company") ? new QCompanyEntity(forProperty("company")) : null;
    }

}

