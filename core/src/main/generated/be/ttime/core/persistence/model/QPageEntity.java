package be.ttime.core.persistence.model;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;
import com.mysema.query.types.path.PathInits;


/**
 * QPageEntity is a Querydsl query type for PageEntity
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QPageEntity extends EntityPathBase<PageEntity> {

    private static final long serialVersionUID = -488812899L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QPageEntity pageEntity = new QPageEntity("pageEntity");

    public final DatePath<java.util.Date> createdDate = createDate("createdDate", java.util.Date.class);

    public final BooleanPath enabled = createBoolean("enabled");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<Integer> level = createNumber("level", Integer.class);

    public final BooleanPath menuItem = createBoolean("menuItem");

    public final BooleanPath menuItemLink = createBoolean("menuItemLink");

    public final DatePath<java.util.Date> modifiedDate = createDate("modifiedDate", java.util.Date.class);

    public final StringPath name = createString("name");

    public final NumberPath<Integer> order = createNumber("order", Integer.class);

    public final ListPath<PageEntity, QPageEntity> pageChildren = this.<PageEntity, QPageEntity>createList("pageChildren", PageEntity.class, QPageEntity.class, PathInits.DIRECT2);

    public final ListPath<ContentEntity, QContentEntity> pageContents = this.<ContentEntity, QContentEntity>createList("pageContents", ContentEntity.class, QContentEntity.class, PathInits.DIRECT2);

    public final QPageEntity pageParent;

    public final QContentTemplateEntity pageTemplate;

    public final SetPath<PrivilegeEntity, QPrivilegeEntity> privileges = this.<PrivilegeEntity, QPrivilegeEntity>createSet("privileges", PrivilegeEntity.class, QPrivilegeEntity.class, PathInits.DIRECT2);

    public final EnumPath<PageEntity.Type> type = createEnum("type", PageEntity.Type.class);

    public QPageEntity(String variable) {
        this(PageEntity.class, forVariable(variable), INITS);
    }

    public QPageEntity(Path<? extends PageEntity> path) {
        this(path.getType(), path.getMetadata(), path.getMetadata().isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QPageEntity(PathMetadata<?> metadata) {
        this(metadata, metadata.isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QPageEntity(PathMetadata<?> metadata, PathInits inits) {
        this(PageEntity.class, metadata, inits);
    }

    public QPageEntity(Class<? extends PageEntity> type, PathMetadata<?> metadata, PathInits inits) {
        super(type, metadata, inits);
        this.pageParent = inits.isInitialized("pageParent") ? new QPageEntity(forProperty("pageParent"), inits.get("pageParent")) : null;
        this.pageTemplate = inits.isInitialized("pageTemplate") ? new QContentTemplateEntity(forProperty("pageTemplate"), inits.get("pageTemplate")) : null;
    }

}

