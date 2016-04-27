package be.ttime.core.persistence.model;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;


/**
 * QBlockTypeEntity is a Querydsl query type for BlockTypeEntity
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QBlockTypeEntity extends EntityPathBase<BlockTypeEntity> {

    private static final long serialVersionUID = 1332704191L;

    public static final QBlockTypeEntity blockTypeEntity = new QBlockTypeEntity("blockTypeEntity");

    public final StringPath name = createString("name");

    public QBlockTypeEntity(String variable) {
        super(BlockTypeEntity.class, forVariable(variable));
    }

    public QBlockTypeEntity(Path<? extends BlockTypeEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QBlockTypeEntity(PathMetadata<?> metadata) {
        super(BlockTypeEntity.class, metadata);
    }

}

