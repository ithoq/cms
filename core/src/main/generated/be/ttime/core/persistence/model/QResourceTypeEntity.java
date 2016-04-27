package be.ttime.core.persistence.model;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;


/**
 * QResourceTypeEntity is a Querydsl query type for ResourceTypeEntity
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QResourceTypeEntity extends EntityPathBase<ResourceTypeEntity> {

    private static final long serialVersionUID = -531891530L;

    public static final QResourceTypeEntity resourceTypeEntity = new QResourceTypeEntity("resourceTypeEntity");

    public final StringPath name = createString("name");

    public QResourceTypeEntity(String variable) {
        super(ResourceTypeEntity.class, forVariable(variable));
    }

    public QResourceTypeEntity(Path<? extends ResourceTypeEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QResourceTypeEntity(PathMetadata<?> metadata) {
        super(ResourceTypeEntity.class, metadata);
    }

}

