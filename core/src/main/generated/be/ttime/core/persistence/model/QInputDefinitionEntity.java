package be.ttime.core.persistence.model;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;


/**
 * QInputDefinitionEntity is a Querydsl query type for InputDefinitionEntity
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QInputDefinitionEntity extends EntityPathBase<InputDefinitionEntity> {

    private static final long serialVersionUID = -1933618475L;

    public static final QInputDefinitionEntity inputDefinitionEntity = new QInputDefinitionEntity("inputDefinitionEntity");

    public final BooleanPath array = createBoolean("array");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath name = createString("name");

    public final StringPath validation = createString("validation");

    public QInputDefinitionEntity(String variable) {
        super(InputDefinitionEntity.class, forVariable(variable));
    }

    public QInputDefinitionEntity(Path<? extends InputDefinitionEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QInputDefinitionEntity(PathMetadata<?> metadata) {
        super(InputDefinitionEntity.class, metadata);
    }

}

