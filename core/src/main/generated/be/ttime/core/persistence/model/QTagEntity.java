package be.ttime.core.persistence.model;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;


/**
 * QTagEntity is a Querydsl query type for TagEntity
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QTagEntity extends EntityPathBase<TagEntity> {

    private static final long serialVersionUID = -1750508686L;

    public static final QTagEntity tagEntity = new QTagEntity("tagEntity");

    public final StringPath name = createString("name");

    public QTagEntity(String variable) {
        super(TagEntity.class, forVariable(variable));
    }

    public QTagEntity(Path<? extends TagEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QTagEntity(PathMetadata<?> metadata) {
        super(TagEntity.class, metadata);
    }

}

