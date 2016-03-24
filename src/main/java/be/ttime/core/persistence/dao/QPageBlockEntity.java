package be.ttime.core.persistence.dao;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;

/**
 * QPageBlockEntity is a Querydsl query type for PageBlockEntity
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QPageBlockEntity extends EntityPathBase<PageBlockEntity> {

    private static final long serialVersionUID = 555670847L;

    public static final QPageBlockEntity pageBlockEntity = new QPageBlockEntity("pageBlockEntity");

    public final EnumPath<PageBlockEntity.BlockType> blockType = createEnum("blockType", PageBlockEntity.BlockType.class);

    public final BooleanPath cacheable = createBoolean("cacheable");

    public final StringPath content = createString("content");

    public final BooleanPath deletable = createBoolean("deletable");

    public final BooleanPath dynamic = createBoolean("dynamic");

    public final BooleanPath enabled = createBoolean("enabled");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath name = createString("name");

    public final ListPath<PageTemplateEntity, QPageTemplateEntity> pageTemplates = this.<PageTemplateEntity, QPageTemplateEntity>createList("pageTemplates", PageTemplateEntity.class, QPageTemplateEntity.class, PathInits.DIRECT2);

    public QPageBlockEntity(String variable) {
        super(PageBlockEntity.class, forVariable(variable));
    }

    public QPageBlockEntity(Path<? extends PageBlockEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QPageBlockEntity(PathMetadata<?> metadata) {
        super(PageBlockEntity.class, metadata);
    }

}

