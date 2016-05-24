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

    public final DatePath<java.util.Date> beginDate = createDate("beginDate", java.util.Date.class);

    public final ListPath<ContentEntity, QContentEntity> contentChildren = this.<ContentEntity, QContentEntity>createList("contentChildren", ContentEntity.class, QContentEntity.class, PathInits.DIRECT2);

    public final QContentEntity contentParent;

    public final QContentTemplateEntity contentTemplate;

    public final QContentTypeEntity contentType;

    public final DatePath<java.util.Date> createdDate = createDate("createdDate", java.util.Date.class);

    public final ListPath<ContentDataEntity, QContentDataEntity> dataList = this.<ContentDataEntity, QContentDataEntity>createList("dataList", ContentDataEntity.class, QContentDataEntity.class, PathInits.DIRECT2);

    public final ListPath<ContentDictionaryEntity, QContentDictionaryEntity> dictionaryList = this.<ContentDictionaryEntity, QContentDictionaryEntity>createList("dictionaryList", ContentDictionaryEntity.class, QContentDictionaryEntity.class, PathInits.DIRECT2);

    public final BooleanPath enabled = createBoolean("enabled");

    public final DatePath<java.util.Date> endDate = createDate("endDate", java.util.Date.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final BooleanPath menuItem = createBoolean("menuItem");

    public final DatePath<java.util.Date> modifiedDate = createDate("modifiedDate", java.util.Date.class);

    public final StringPath name = createString("name");

    public final NumberPath<Integer> order = createNumber("order", Integer.class);

    public final SetPath<PrivilegeEntity, QPrivilegeEntity> privileges = this.<PrivilegeEntity, QPrivilegeEntity>createSet("privileges", PrivilegeEntity.class, QPrivilegeEntity.class, PathInits.DIRECT2);

    public final ListPath<TaxonomyTermEntity, QTaxonomyTermEntity> taxonomyTermEntities = this.<TaxonomyTermEntity, QTaxonomyTermEntity>createList("taxonomyTermEntities", TaxonomyTermEntity.class, QTaxonomyTermEntity.class, PathInits.DIRECT2);

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

