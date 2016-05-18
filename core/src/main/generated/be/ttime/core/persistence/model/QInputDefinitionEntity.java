package be.ttime.core.persistence.model;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;
import com.mysema.query.types.path.PathInits;


/**
 * QInputDefinitionEntity is a Querydsl query type for InputDefinitionEntity
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QInputDefinitionEntity extends EntityPathBase<InputDefinitionEntity> {

    private static final long serialVersionUID = -1933618475L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QInputDefinitionEntity inputDefinitionEntity = new QInputDefinitionEntity("inputDefinitionEntity");

    public final BooleanPath array = createBoolean("array");

    public final QFieldsetEntity fieldset;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath name = createString("name");

    public final NumberPath<Integer> sort = createNumber("sort", Integer.class);

    public final StringPath validation = createString("validation");

    public QInputDefinitionEntity(String variable) {
        this(InputDefinitionEntity.class, forVariable(variable), INITS);
    }

    public QInputDefinitionEntity(Path<? extends InputDefinitionEntity> path) {
        this(path.getType(), path.getMetadata(), path.getMetadata().isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QInputDefinitionEntity(PathMetadata<?> metadata) {
        this(metadata, metadata.isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QInputDefinitionEntity(PathMetadata<?> metadata, PathInits inits) {
        this(InputDefinitionEntity.class, metadata, inits);
    }

    public QInputDefinitionEntity(Class<? extends InputDefinitionEntity> type, PathMetadata<?> metadata, PathInits inits) {
        super(type, metadata, inits);
        this.fieldset = inits.isInitialized("fieldset") ? new QFieldsetEntity(forProperty("fieldset"), inits.get("fieldset")) : null;
    }

}

