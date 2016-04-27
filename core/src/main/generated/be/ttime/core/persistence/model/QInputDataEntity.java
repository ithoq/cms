package be.ttime.core.persistence.model;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;


/**
 * QInputDataEntity is a Querydsl query type for InputDataEntity
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QInputDataEntity extends EntityPathBase<InputDataEntity> {

    private static final long serialVersionUID = -568317140L;

    public static final QInputDataEntity inputDataEntity = new QInputDataEntity("inputDataEntity");

    public final StringPath hint = createString("hint");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath title = createString("title");

    public final StringPath validation = createString("validation");

    public QInputDataEntity(String variable) {
        super(InputDataEntity.class, forVariable(variable));
    }

    public QInputDataEntity(Path<? extends InputDataEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QInputDataEntity(PathMetadata<?> metadata) {
        super(InputDataEntity.class, metadata);
    }

}

