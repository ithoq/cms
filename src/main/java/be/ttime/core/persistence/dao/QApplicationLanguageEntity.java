package be.ttime.core.persistence.dao;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;


/**
 * QApplicationLanguageEntity is a Querydsl query type for ApplicationLanguageEntity
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QApplicationLanguageEntity extends EntityPathBase<ApplicationLanguageEntity> {

    private static final long serialVersionUID = -1083702839L;

    public static final QApplicationLanguageEntity applicationLanguageEntity = new QApplicationLanguageEntity("applicationLanguageEntity");

    public final BooleanPath enabled = createBoolean("enabled");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath iso = createString("iso");

    public QApplicationLanguageEntity(String variable) {
        super(ApplicationLanguageEntity.class, forVariable(variable));
    }

    public QApplicationLanguageEntity(Path<? extends ApplicationLanguageEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QApplicationLanguageEntity(PathMetadata<?> metadata) {
        super(ApplicationLanguageEntity.class, metadata);
    }

}

