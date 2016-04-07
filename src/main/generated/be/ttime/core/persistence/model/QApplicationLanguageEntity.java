package be.ttime.core.persistence.model;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;
import com.mysema.query.types.path.PathInits;


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

    public final ListPath<PageBlockEntity, QPageBlockEntity> pageBlocks = this.<PageBlockEntity, QPageBlockEntity>createList("pageBlocks", PageBlockEntity.class, QPageBlockEntity.class, PathInits.DIRECT2);

    public final ListPath<PageContentEntity, QPageContentEntity> pageContents = this.<PageContentEntity, QPageContentEntity>createList("pageContents", PageContentEntity.class, QPageContentEntity.class, PathInits.DIRECT2);

    public final BooleanPath rtl = createBoolean("rtl");

    public final ListPath<MessageTranslationsEntity, QMessageTranslationsEntity> translations = this.<MessageTranslationsEntity, QMessageTranslationsEntity>createList("translations", MessageTranslationsEntity.class, QMessageTranslationsEntity.class, PathInits.DIRECT2);

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

