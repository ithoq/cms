package be.ttime.core.persistence.model;

import com.mysema.query.types.Path;
import com.mysema.query.types.PathMetadata;
import com.mysema.query.types.path.*;

import javax.annotation.Generated;

import static com.mysema.query.types.PathMetadataFactory.forVariable;


/**
 * QPageBlockEntity is a Querydsl query type for PageBlockEntity
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QPageBlockEntity extends EntityPathBase<PageBlockEntity> {

    private static final long serialVersionUID = 1522430038L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QPageBlockEntity pageBlockEntity = new QPageBlockEntity("pageBlockEntity");

    public final EnumPath<PageBlockEntity.BlockType> blockType = createEnum("blockType", PageBlockEntity.BlockType.class);

    public final BooleanPath cacheable = createBoolean("cacheable");

    public final StringPath content = createString("content");

    public final BooleanPath deletable = createBoolean("deletable");

    public final BooleanPath dynamic = createBoolean("dynamic");

    public final BooleanPath enabled = createBoolean("enabled");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QApplicationLanguageEntity language;

    public final StringPath name = createString("name");

    public final ListPath<PageTemplateEntity, QPageTemplateEntity> pageTemplates = this.<PageTemplateEntity, QPageTemplateEntity>createList("pageTemplates", PageTemplateEntity.class, QPageTemplateEntity.class, PathInits.DIRECT2);

    public QPageBlockEntity(String variable) {
        this(PageBlockEntity.class, forVariable(variable), INITS);
    }

    public QPageBlockEntity(Path<? extends PageBlockEntity> path) {
        this(path.getType(), path.getMetadata(), path.getMetadata().isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QPageBlockEntity(PathMetadata<?> metadata) {
        this(metadata, metadata.isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QPageBlockEntity(PathMetadata<?> metadata, PathInits inits) {
        this(PageBlockEntity.class, metadata, inits);
    }

    public QPageBlockEntity(Class<? extends PageBlockEntity> type, PathMetadata<?> metadata, PathInits inits) {
        super(type, metadata, inits);
        this.language = inits.isInitialized("language") ? new QApplicationLanguageEntity(forProperty("language")) : null;
    }

}

