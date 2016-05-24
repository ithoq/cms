package be.ttime.core.persistence.model;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;


/**
 * QContentTypeEntity is a Querydsl query type for ContentTypeEntity
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QContentTypeEntity extends EntityPathBase<ContentTypeEntity> {

    private static final long serialVersionUID = 960387947L;

    public static final QContentTypeEntity contentTypeEntity = new QContentTypeEntity("contentTypeEntity");

    public final StringPath name = createString("name");

    public QContentTypeEntity(String variable) {
        super(ContentTypeEntity.class, forVariable(variable));
    }

    public QContentTypeEntity(Path<? extends ContentTypeEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QContentTypeEntity(PathMetadata<?> metadata) {
        super(ContentTypeEntity.class, metadata);
    }

}

