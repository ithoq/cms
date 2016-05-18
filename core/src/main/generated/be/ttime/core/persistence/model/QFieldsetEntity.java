package be.ttime.core.persistence.model;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;
import com.mysema.query.types.path.PathInits;


/**
 * QFieldsetEntity is a Querydsl query type for FieldsetEntity
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QFieldsetEntity extends EntityPathBase<FieldsetEntity> {

    private static final long serialVersionUID = 2110519542L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QFieldsetEntity fieldsetEntity = new QFieldsetEntity("fieldsetEntity");

    public final QBlockEntity blockEntity;

    public final ListPath<ContentTemplateFieldsetEntity, QContentTemplateFieldsetEntity> contentTemplateFieldset = this.<ContentTemplateFieldsetEntity, QContentTemplateFieldsetEntity>createList("contentTemplateFieldset", ContentTemplateFieldsetEntity.class, QContentTemplateFieldsetEntity.class, PathInits.DIRECT2);

    public final StringPath description = createString("description");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final ListPath<InputDefinitionEntity, QInputDefinitionEntity> inputs = this.<InputDefinitionEntity, QInputDefinitionEntity>createList("inputs", InputDefinitionEntity.class, QInputDefinitionEntity.class, PathInits.DIRECT2);

    public final StringPath name = createString("name");

    public final StringPath namespace = createString("namespace");

    public QFieldsetEntity(String variable) {
        this(FieldsetEntity.class, forVariable(variable), INITS);
    }

    public QFieldsetEntity(Path<? extends FieldsetEntity> path) {
        this(path.getType(), path.getMetadata(), path.getMetadata().isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QFieldsetEntity(PathMetadata<?> metadata) {
        this(metadata, metadata.isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QFieldsetEntity(PathMetadata<?> metadata, PathInits inits) {
        this(FieldsetEntity.class, metadata, inits);
    }

    public QFieldsetEntity(Class<? extends FieldsetEntity> type, PathMetadata<?> metadata, PathInits inits) {
        super(type, metadata, inits);
        this.blockEntity = inits.isInitialized("blockEntity") ? new QBlockEntity(forProperty("blockEntity"), inits.get("blockEntity")) : null;
    }

}

