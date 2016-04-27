package be.ttime.core.persistence.model;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;
import com.mysema.query.types.path.PathInits;


/**
 * QResourceEntity is a Querydsl query type for ResourceEntity
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QResourceEntity extends EntityPathBase<ResourceEntity> {

    private static final long serialVersionUID = 1072084828L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QResourceEntity resourceEntity = new QResourceEntity("resourceEntity");

    public final QCategoryEntity category;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QResourceTypeEntity resourceType;

    public final CollectionPath<TagEntity, QTagEntity> tags = this.<TagEntity, QTagEntity>createCollection("tags", TagEntity.class, QTagEntity.class, PathInits.DIRECT2);

    public QResourceEntity(String variable) {
        this(ResourceEntity.class, forVariable(variable), INITS);
    }

    public QResourceEntity(Path<? extends ResourceEntity> path) {
        this(path.getType(), path.getMetadata(), path.getMetadata().isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QResourceEntity(PathMetadata<?> metadata) {
        this(metadata, metadata.isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QResourceEntity(PathMetadata<?> metadata, PathInits inits) {
        this(ResourceEntity.class, metadata, inits);
    }

    public QResourceEntity(Class<? extends ResourceEntity> type, PathMetadata<?> metadata, PathInits inits) {
        super(type, metadata, inits);
        this.category = inits.isInitialized("category") ? new QCategoryEntity(forProperty("category")) : null;
        this.resourceType = inits.isInitialized("resourceType") ? new QResourceTypeEntity(forProperty("resourceType")) : null;
    }

}

