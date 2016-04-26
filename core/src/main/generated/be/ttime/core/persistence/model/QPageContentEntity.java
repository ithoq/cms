package be.ttime.core.persistence.model;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;
import com.mysema.query.types.path.PathInits;


/**
 * QPageContentEntity is a Querydsl query type for PageContentEntity
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QPageContentEntity extends EntityPathBase<ContentEntity> {

    private static final long serialVersionUID = 1701115330L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QPageContentEntity pageContentEntity = new QPageContentEntity("pageContentEntity");

    public final StringPath computedSlug = createString("computedSlug");

    public final DatePath<java.util.Date> createdDate = createDate("createdDate", java.util.Date.class);

    public final StringPath data = createString("data");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QApplicationLanguageEntity language;

    public final DatePath<java.util.Date> modifiedDate = createDate("modifiedDate", java.util.Date.class);

    public final QPageEntity page;

    public final ListPath<FileEntity, QFileEntity> pageFiles = this.<FileEntity, QFileEntity>createList("pageFiles", FileEntity.class, QFileEntity.class, PathInits.DIRECT2);

    public final StringPath seoDescription = createString("seoDescription");

    public final StringPath seoH1 = createString("seoH1");

    public final StringPath seoTag = createString("seoTag");

    public final StringPath seoTitle = createString("seoTitle");

    public final StringPath slug = createString("slug");

    public final StringPath test = createString("test");

    public QPageContentEntity(String variable) {
        this(ContentEntity.class, forVariable(variable), INITS);
    }

    public QPageContentEntity(Path<? extends ContentEntity> path) {
        this(path.getType(), path.getMetadata(), path.getMetadata().isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QPageContentEntity(PathMetadata<?> metadata) {
        this(metadata, metadata.isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QPageContentEntity(PathMetadata<?> metadata, PathInits inits) {
        this(ContentEntity.class, metadata, inits);
    }

    public QPageContentEntity(Class<? extends ContentEntity> type, PathMetadata<?> metadata, PathInits inits) {
        super(type, metadata, inits);
        this.language = inits.isInitialized("language") ? new QApplicationLanguageEntity(forProperty("language")) : null;
        this.page = inits.isInitialized("page") ? new QPageEntity(forProperty("page"), inits.get("page")) : null;
    }

}

