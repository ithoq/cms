package be.ttime.core.persistence.model;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;
import com.mysema.query.types.path.PathInits;


/**
 * QTaxonomyTermDataEntity is a Querydsl query type for TaxonomyTermDataEntity
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QTaxonomyTermDataEntity extends EntityPathBase<TaxonomyTermDataEntity> {

    private static final long serialVersionUID = 338668085L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QTaxonomyTermDataEntity taxonomyTermDataEntity = new QTaxonomyTermDataEntity("taxonomyTermDataEntity");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath name = createString("name");

    public final StringPath slug = createString("slug");

    public final QTaxonomyTermEntity taxonomyTerm;

    public QTaxonomyTermDataEntity(String variable) {
        this(TaxonomyTermDataEntity.class, forVariable(variable), INITS);
    }

    public QTaxonomyTermDataEntity(Path<? extends TaxonomyTermDataEntity> path) {
        this(path.getType(), path.getMetadata(), path.getMetadata().isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QTaxonomyTermDataEntity(PathMetadata<?> metadata) {
        this(metadata, metadata.isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QTaxonomyTermDataEntity(PathMetadata<?> metadata, PathInits inits) {
        this(TaxonomyTermDataEntity.class, metadata, inits);
    }

    public QTaxonomyTermDataEntity(Class<? extends TaxonomyTermDataEntity> type, PathMetadata<?> metadata, PathInits inits) {
        super(type, metadata, inits);
        this.taxonomyTerm = inits.isInitialized("taxonomyTerm") ? new QTaxonomyTermEntity(forProperty("taxonomyTerm"), inits.get("taxonomyTerm")) : null;
    }

}

