package be.ttime.core.persistence.model;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;
import com.mysema.query.types.path.PathInits;


/**
 * QMenuItemEntity is a Querydsl query type for MenuItemEntity
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QMenuItemEntity extends EntityPathBase<MenuItemEntity> {

    private static final long serialVersionUID = -799842976L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QMenuItemEntity menuItemEntity = new QMenuItemEntity("menuItemEntity");

    public final QContentEntity content;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QMenuEntity menu;

    public final ListPath<MenuItemEntity, QMenuItemEntity> menuItemChildrens = this.<MenuItemEntity, QMenuItemEntity>createList("menuItemChildrens", MenuItemEntity.class, QMenuItemEntity.class, PathInits.DIRECT2);

    public final QMenuItemEntity menuItemParent;

    public final NumberPath<Integer> position = createNumber("position", Integer.class);

    public QMenuItemEntity(String variable) {
        this(MenuItemEntity.class, forVariable(variable), INITS);
    }

    public QMenuItemEntity(Path<? extends MenuItemEntity> path) {
        this(path.getType(), path.getMetadata(), path.getMetadata().isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QMenuItemEntity(PathMetadata<?> metadata) {
        this(metadata, metadata.isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QMenuItemEntity(PathMetadata<?> metadata, PathInits inits) {
        this(MenuItemEntity.class, metadata, inits);
    }

    public QMenuItemEntity(Class<? extends MenuItemEntity> type, PathMetadata<?> metadata, PathInits inits) {
        super(type, metadata, inits);
        this.content = inits.isInitialized("content") ? new QContentEntity(forProperty("content"), inits.get("content")) : null;
        this.menu = inits.isInitialized("menu") ? new QMenuEntity(forProperty("menu")) : null;
        this.menuItemParent = inits.isInitialized("menuItemParent") ? new QMenuItemEntity(forProperty("menuItemParent"), inits.get("menuItemParent")) : null;
    }

}

