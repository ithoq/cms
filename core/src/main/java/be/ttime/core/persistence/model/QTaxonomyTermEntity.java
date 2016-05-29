package be.ttime.core.persistence.model;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;
import com.mysema.query.types.path.PathInits;


/**
 * QTaxonomyTermEntity is a Querydsl query type for TaxonomyTermEntity
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QTaxonomyTermEntity extends EntityPathBase<TaxonomyTermEntity> {

    private static final long serialVersionUID = 1718745579L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QTaxonomyTermEntity taxonomyTermEntity = new QTaxonomyTermEntity("taxonomyTermEntity");

    public final ListPath<ContentEntity, QContentEntity> contents = this.<ContentEntity, QContentEntity>createList("contents", ContentEntity.class, QContentEntity.class, PathInits.DIRECT2);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath name = createString("name");

    public final NumberPath<Integer> position = createNumber("position", Integer.class);

    public final ListPath<TaxonomyTermEntity, QTaxonomyTermEntity> taxonomyChildren = this.<TaxonomyTermEntity, QTaxonomyTermEntity>createList("taxonomyChildren", TaxonomyTermEntity.class, QTaxonomyTermEntity.class, PathInits.DIRECT2);

    public final QTaxonomyTermEntity taxonomyParent;

    public final QTaxonomyTypeEntity taxonomyType;

    public final ListPath<TaxonomyTermDataEntity, QTaxonomyTermDataEntity> termDataList = this.<TaxonomyTermDataEntity, QTaxonomyTermDataEntity>createList("termDataList", TaxonomyTermDataEntity.class, QTaxonomyTermDataEntity.class, PathInits.DIRECT2);

    public QTaxonomyTermEntity(String variable) {
        this(TaxonomyTermEntity.class, forVariable(variable), INITS);
    }

    public QTaxonomyTermEntity(Path<? extends TaxonomyTermEntity> path) {
        this(path.getType(), path.getMetadata(), path.getMetadata().isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QTaxonomyTermEntity(PathMetadata<?> metadata) {
        this(metadata, metadata.isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QTaxonomyTermEntity(PathMetadata<?> metadata, PathInits inits) {
        this(TaxonomyTermEntity.class, metadata, inits);
    }

    public QTaxonomyTermEntity(Class<? extends TaxonomyTermEntity> type, PathMetadata<?> metadata, PathInits inits) {
        super(type, metadata, inits);
        this.taxonomyParent = inits.isInitialized("taxonomyParent") ? new QTaxonomyTermEntity(forProperty("taxonomyParent"), inits.get("taxonomyParent")) : null;
        this.taxonomyType = inits.isInitialized("taxonomyType") ? new QTaxonomyTypeEntity(forProperty("taxonomyType")) : null;
    }

}

