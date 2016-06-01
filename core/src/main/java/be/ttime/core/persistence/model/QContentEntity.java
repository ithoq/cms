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

    public final be.ttime.core.persistence.QAbstractTimestampEntity _super = new be.ttime.core.persistence.QAbstractTimestampEntity(this);

    public final DateTimePath<java.util.Date> beginDate = createDateTime("beginDate", java.util.Date.class);

    public final SetPath<ContentEntity, QContentEntity> contentChildren = this.<ContentEntity, QContentEntity>createSet("contentChildren", ContentEntity.class, QContentEntity.class, PathInits.DIRECT2);

    public final QContentEntity contentParent;

    public final QContentTemplateEntity contentTemplate;

    public final QContentTypeEntity contentType;

    //inherited
    public final DateTimePath<java.util.Date> created = _super.created;

    public final SetPath<ContentDataEntity, QContentDataEntity> dataList = this.<ContentDataEntity, QContentDataEntity>createSet("dataList", ContentDataEntity.class, QContentDataEntity.class, PathInits.DIRECT2);

    public final SetPath<ContentDictionaryEntity, QContentDictionaryEntity> dictionaryList = this.<ContentDictionaryEntity, QContentDictionaryEntity>createSet("dictionaryList", ContentDictionaryEntity.class, QContentDictionaryEntity.class, PathInits.DIRECT2);

    public final BooleanPath enabled = createBoolean("enabled");

    public final DateTimePath<java.util.Date> endDate = createDateTime("endDate", java.util.Date.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final BooleanPath menuItem = createBoolean("menuItem");

    public final StringPath name = createString("name");

    public final NumberPath<Integer> order = createNumber("order", Integer.class);

    public final SetPath<PrivilegeEntity, QPrivilegeEntity> privileges = this.<PrivilegeEntity, QPrivilegeEntity>createSet("privileges", PrivilegeEntity.class, QPrivilegeEntity.class, PathInits.DIRECT2);

    public final SetPath<TaxonomyTermEntity, QTaxonomyTermEntity> taxonomyTermEntities = this.<TaxonomyTermEntity, QTaxonomyTermEntity>createSet("taxonomyTermEntities", TaxonomyTermEntity.class, QTaxonomyTermEntity.class, PathInits.DIRECT2);

    //inherited
    public final DateTimePath<java.util.Date> updated = _super.updated;

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
        this.contentParent = inits.isInitialized("contentParent") ? new QContentEntity(forProperty("contentParent"), inits.get("contentParent")) : null;
        this.contentTemplate = inits.isInitialized("contentTemplate") ? new QContentTemplateEntity(forProperty("contentTemplate"), inits.get("contentTemplate")) : null;
        this.contentType = inits.isInitialized("contentType") ? new QContentTypeEntity(forProperty("contentType")) : null;
    }

}

