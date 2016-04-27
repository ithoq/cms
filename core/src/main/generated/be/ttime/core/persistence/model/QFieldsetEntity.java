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

    public static final QFieldsetEntity fieldsetEntity = new QFieldsetEntity("fieldsetEntity");

    public final SetPath<ContentTemplateFieldsetEntity, QContentTemplateFieldsetEntity> contentTemplateFieldset = this.<ContentTemplateFieldsetEntity, QContentTemplateFieldsetEntity>createSet("contentTemplateFieldset", ContentTemplateFieldsetEntity.class, QContentTemplateFieldsetEntity.class, PathInits.DIRECT2);

    public final StringPath description = createString("description");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath name = createString("name");

    public QFieldsetEntity(String variable) {
        super(FieldsetEntity.class, forVariable(variable));
    }

    public QFieldsetEntity(Path<? extends FieldsetEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QFieldsetEntity(PathMetadata<?> metadata) {
        super(FieldsetEntity.class, metadata);
    }

}

