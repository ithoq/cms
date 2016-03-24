package be.ttime.core.persistence.dao;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;

/**
 * QPageTemplateEntity is a Querydsl query type for PageTemplateEntity
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QPageTemplateEntity extends EntityPathBase<PageTemplateEntity> {

    private static final long serialVersionUID = 1723656238L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QPageTemplateEntity pageTemplateEntity = new QPageTemplateEntity("pageTemplateEntity");

    public final BooleanPath active = createBoolean("active");

    public final StringPath fields = createString("fields");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath name = createString("name");

    public final QPageBlockEntity pageBlock;

    public final ListPath<PageEntity, QPageEntity> pageEntities = this.<PageEntity, QPageEntity>createList("pageEntities", PageEntity.class, QPageEntity.class, PathInits.DIRECT2);

    public QPageTemplateEntity(String variable) {
        this(PageTemplateEntity.class, forVariable(variable), INITS);
    }

    public QPageTemplateEntity(Path<? extends PageTemplateEntity> path) {
        this(path.getType(), path.getMetadata(), path.getMetadata().isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QPageTemplateEntity(PathMetadata<?> metadata) {
        this(metadata, metadata.isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QPageTemplateEntity(PathMetadata<?> metadata, PathInits inits) {
        this(PageTemplateEntity.class, metadata, inits);
    }

    public QPageTemplateEntity(Class<? extends PageTemplateEntity> type, PathMetadata<?> metadata, PathInits inits) {
        super(type, metadata, inits);
        this.pageBlock = inits.isInitialized("pageBlock") ? new QPageBlockEntity(forProperty("pageBlock")) : null;
    }

}

