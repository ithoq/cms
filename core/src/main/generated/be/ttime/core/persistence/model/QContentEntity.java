package be.ttime.core.persistence.model;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;
import com.mysema.query.types.path.PathInits;


/**
 * QContentEntity is a Querydsl query type for ContentEntity
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QContentEntity extends EntityPathBase<ContentEntity> {

    private static final long serialVersionUID = 1948760721L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QContentEntity contentEntity = new QContentEntity("contentEntity");

    public final StringPath computedSlug = createString("computedSlug");

    public final DatePath<java.util.Date> createdDate = createDate("createdDate", java.util.Date.class);

    public final StringPath data = createString("data");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QApplicationLanguageEntity language;

    public final DatePath<java.util.Date> modifiedDate = createDate("modifiedDate", java.util.Date.class);

    public final QPageEntity page;

    public final ListPath<FileEntity, QFileEntity> pageFiles = this.<FileEntity, QFileEntity>createList("pageFiles", FileEntity.class, QFileEntity.class, PathInits.DIRECT2);

    public final QResourceTypeEntity resourceType;

    public final StringPath seoDescription = createString("seoDescription");

    public final StringPath seoH1 = createString("seoH1");

    public final StringPath seoTag = createString("seoTag");

    public final StringPath seoTitle = createString("seoTitle");

    public final StringPath slug = createString("slug");

    public QContentEntity(String variable) {
        this(ContentEntity.class, forVariable(variable), INITS);
    }

    public QContentEntity(Path<? extends ContentEntity> path) {
        this(path.getType(), path.getMetadata(), path.getMetadata().isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QContentEntity(PathMetadata<?> metadata) {
        this(metadata, metadata.isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QContentEntity(PathMetadata<?> metadata, PathInits inits) {
        this(ContentEntity.class, metadata, inits);
    }

    public QContentEntity(Class<? extends ContentEntity> type, PathMetadata<?> metadata, PathInits inits) {
        super(type, metadata, inits);
        this.language = inits.isInitialized("language") ? new QApplicationLanguageEntity(forProperty("language")) : null;
        this.page = inits.isInitialized("page") ? new QPageEntity(forProperty("page"), inits.get("page")) : null;
        this.resourceType = inits.isInitialized("resourceType") ? new QResourceTypeEntity(forProperty("resourceType")) : null;
    }

}

