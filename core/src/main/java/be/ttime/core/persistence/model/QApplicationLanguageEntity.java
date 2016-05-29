package be.ttime.core.persistence.model;

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

    private static final long serialVersionUID = 1903682592L;

    public static final QApplicationLanguageEntity applicationLanguageEntity = new QApplicationLanguageEntity("applicationLanguageEntity");

    public final BooleanPath enabledForAdmin = createBoolean("enabledForAdmin");

    public final BooleanPath enabledForPublic = createBoolean("enabledForPublic");

    public final StringPath locale = createString("locale");

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

