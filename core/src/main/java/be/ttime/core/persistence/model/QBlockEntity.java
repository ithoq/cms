package be.ttime.core.persistence.model;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;
import com.mysema.query.types.path.PathInits;


/**
 * QBlockEntity is a Querydsl query type for BlockEntity
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QBlockEntity extends EntityPathBase<BlockEntity> {

    private static final long serialVersionUID = 968498405L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QBlockEntity blockEntity = new QBlockEntity("blockEntity");

    public final QBlockTypeEntity blockType;

    public final BooleanPath cacheable = createBoolean("cacheable");

    public final StringPath content = createString("content");

    public final BooleanPath deletable = createBoolean("deletable");

    public final StringPath displayName = createString("displayName");

    public final BooleanPath dynamic = createBoolean("dynamic");

    public final BooleanPath enabled = createBoolean("enabled");

    public final QApplicationLanguageEntity language;

    public final StringPath name = createString("name");

    public QBlockEntity(String variable) {
        this(BlockEntity.class, forVariable(variable), INITS);
    }

    public QBlockEntity(Path<? extends BlockEntity> path) {
        this(path.getType(), path.getMetadata(), path.getMetadata().isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QBlockEntity(PathMetadata<?> metadata) {
        this(metadata, metadata.isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QBlockEntity(PathMetadata<?> metadata, PathInits inits) {
        this(BlockEntity.class, metadata, inits);
    }

    public QBlockEntity(Class<? extends BlockEntity> type, PathMetadata<?> metadata, PathInits inits) {
        super(type, metadata, inits);
        this.blockType = inits.isInitialized("blockType") ? new QBlockTypeEntity(forProperty("blockType")) : null;
        this.language = inits.isInitialized("language") ? new QApplicationLanguageEntity(forProperty("language")) : null;
    }

}

