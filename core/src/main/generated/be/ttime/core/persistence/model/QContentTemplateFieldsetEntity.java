package be.ttime.core.persistence.model;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;
import com.mysema.query.types.path.PathInits;


/**
 * QContentTemplateFieldsetEntity is a Querydsl query type for ContentTemplateFieldsetEntity
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QContentTemplateFieldsetEntity extends EntityPathBase<ContentTemplateFieldsetEntity> {

    private static final long serialVersionUID = -115865357L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QContentTemplateFieldsetEntity contentTemplateFieldsetEntity = new QContentTemplateFieldsetEntity("contentTemplateFieldsetEntity");

    public final QContentTemplateEntity contentTemplate;

    public final ListPath<InputDataEntity, QInputDataEntity> dataEntities = this.<InputDataEntity, QInputDataEntity>createList("dataEntities", InputDataEntity.class, QInputDataEntity.class, PathInits.DIRECT2);

    public final QFieldsetEntity fieldset;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath name = createString("name");

    public final StringPath namespace = createString("namespace");

    public final NumberPath<Integer> position = createNumber("position", Integer.class);

    public QContentTemplateFieldsetEntity(String variable) {
        this(ContentTemplateFieldsetEntity.class, forVariable(variable), INITS);
    }

    public QContentTemplateFieldsetEntity(Path<? extends ContentTemplateFieldsetEntity> path) {
        this(path.getType(), path.getMetadata(), path.getMetadata().isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QContentTemplateFieldsetEntity(PathMetadata<?> metadata) {
        this(metadata, metadata.isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QContentTemplateFieldsetEntity(PathMetadata<?> metadata, PathInits inits) {
        this(ContentTemplateFieldsetEntity.class, metadata, inits);
    }

    public QContentTemplateFieldsetEntity(Class<? extends ContentTemplateFieldsetEntity> type, PathMetadata<?> metadata, PathInits inits) {
        super(type, metadata, inits);
        this.contentTemplate = inits.isInitialized("contentTemplate") ? new QContentTemplateEntity(forProperty("contentTemplate"), inits.get("contentTemplate")) : null;
        this.fieldset = inits.isInitialized("fieldset") ? new QFieldsetEntity(forProperty("fieldset"), inits.get("fieldset")) : null;
    }

}

