package be.ttime.core.persistence.model;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;
import com.mysema.query.types.path.PathInits;


/**
 * QInputDataEntity is a Querydsl query type for InputDataEntity
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QInputDataEntity extends EntityPathBase<InputDataEntity> {

    private static final long serialVersionUID = -568317140L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QInputDataEntity inputDataEntity = new QInputDataEntity("inputDataEntity");

    public final QContentTemplateFieldsetEntity contentTemplateFieldsetEntity;

    public final StringPath defaultValue = createString("defaultValue");

    public final QFieldsetEntity fieldset;

    public final StringPath hint = createString("hint");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QInputDefinitionEntity inputDefinition;

    public final StringPath title = createString("title");

    public final StringPath validation = createString("validation");

    public QInputDataEntity(String variable) {
        this(InputDataEntity.class, forVariable(variable), INITS);
    }

    public QInputDataEntity(Path<? extends InputDataEntity> path) {
        this(path.getType(), path.getMetadata(), path.getMetadata().isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QInputDataEntity(PathMetadata<?> metadata) {
        this(metadata, metadata.isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QInputDataEntity(PathMetadata<?> metadata, PathInits inits) {
        this(InputDataEntity.class, metadata, inits);
    }

    public QInputDataEntity(Class<? extends InputDataEntity> type, PathMetadata<?> metadata, PathInits inits) {
        super(type, metadata, inits);
        this.contentTemplateFieldsetEntity = inits.isInitialized("contentTemplateFieldsetEntity") ? new QContentTemplateFieldsetEntity(forProperty("contentTemplateFieldsetEntity"), inits.get("contentTemplateFieldsetEntity")) : null;
        this.fieldset = inits.isInitialized("fieldset") ? new QFieldsetEntity(forProperty("fieldset"), inits.get("fieldset")) : null;
        this.inputDefinition = inits.isInitialized("inputDefinition") ? new QInputDefinitionEntity(forProperty("inputDefinition"), inits.get("inputDefinition")) : null;
    }

}

