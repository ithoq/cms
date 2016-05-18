package be.ttime.core.persistence.model;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;
import com.mysema.query.types.path.PathInits;


/**
 * QContentTemplateEntity is a Querydsl query type for ContentTemplateEntity
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QContentTemplateEntity extends EntityPathBase<ContentTemplateEntity> {

    private static final long serialVersionUID = -1434181205L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QContentTemplateEntity contentTemplateEntity = new QContentTemplateEntity("contentTemplateEntity");

    public final BooleanPath active = createBoolean("active");

    public final QBlockEntity block;

    public final ListPath<ContentTemplateFieldsetEntity, QContentTemplateFieldsetEntity> contentTemplateFieldset = this.<ContentTemplateFieldsetEntity, QContentTemplateFieldsetEntity>createList("contentTemplateFieldset", ContentTemplateFieldsetEntity.class, QContentTemplateFieldsetEntity.class, PathInits.DIRECT2);

    public final StringPath description = createString("description");

    public final StringPath fields = createString("fields");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath name = createString("name");

    public final ListPath<PageEntity, QPageEntity> pageEntities = this.<PageEntity, QPageEntity>createList("pageEntities", PageEntity.class, QPageEntity.class, PathInits.DIRECT2);

    public QContentTemplateEntity(String variable) {
        this(ContentTemplateEntity.class, forVariable(variable), INITS);
    }

    public QContentTemplateEntity(Path<? extends ContentTemplateEntity> path) {
        this(path.getType(), path.getMetadata(), path.getMetadata().isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QContentTemplateEntity(PathMetadata<?> metadata) {
        this(metadata, metadata.isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QContentTemplateEntity(PathMetadata<?> metadata, PathInits inits) {
        this(ContentTemplateEntity.class, metadata, inits);
    }

    public QContentTemplateEntity(Class<? extends ContentTemplateEntity> type, PathMetadata<?> metadata, PathInits inits) {
        super(type, metadata, inits);
        this.block = inits.isInitialized("block") ? new QBlockEntity(forProperty("block"), inits.get("block")) : null;
    }

}

