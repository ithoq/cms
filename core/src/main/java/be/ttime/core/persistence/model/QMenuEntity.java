package be.ttime.core.persistence.model;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;


/**
 * QMenuEntity is a Querydsl query type for MenuEntity
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QMenuEntity extends EntityPathBase<MenuEntity> {

    private static final long serialVersionUID = 2090771821L;

    public static final QMenuEntity menuEntity = new QMenuEntity("menuEntity");

    public final StringPath name = createString("name");

    public QMenuEntity(String variable) {
        super(MenuEntity.class, forVariable(variable));
    }

    public QMenuEntity(Path<? extends MenuEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QMenuEntity(PathMetadata<?> metadata) {
        super(MenuEntity.class, metadata);
    }

}

