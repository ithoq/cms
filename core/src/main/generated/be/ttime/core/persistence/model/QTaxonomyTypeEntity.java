package be.ttime.core.persistence.model;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;


/**
 * QTaxonomyTypeEntity is a Querydsl query type for TaxonomyTypeEntity
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QTaxonomyTypeEntity extends EntityPathBase<TaxonomyTypeEntity> {

    private static final long serialVersionUID = -2066320839L;

    public static final QTaxonomyTypeEntity taxonomyTypeEntity = new QTaxonomyTypeEntity("taxonomyTypeEntity");

    public final StringPath name = createString("name");

    public QTaxonomyTypeEntity(String variable) {
        super(TaxonomyTypeEntity.class, forVariable(variable));
    }

    public QTaxonomyTypeEntity(Path<? extends TaxonomyTypeEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QTaxonomyTypeEntity(PathMetadata<?> metadata) {
        super(TaxonomyTypeEntity.class, metadata);
    }

}

