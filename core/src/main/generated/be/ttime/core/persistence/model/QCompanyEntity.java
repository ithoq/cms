package be.ttime.core.persistence.model;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;


/**
 * QCompanyEntity is a Querydsl query type for CompanyEntity
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QCompanyEntity extends EntityPathBase<CompanyEntity> {

    private static final long serialVersionUID = -980766827L;

    public static final QCompanyEntity companyEntity = new QCompanyEntity("companyEntity");

    public final StringPath city = createString("city");

    public final StringPath comment = createString("comment");

    public final StringPath countyCode = createString("countyCode");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath legalForm = createString("legalForm");

    public final StringPath nace = createString("nace");

    public final StringPath name = createString("name");

    public final StringPath street1 = createString("street1");

    public final StringPath street2 = createString("street2");

    public final StringPath street3 = createString("street3");

    public final StringPath website = createString("website");

    public final StringPath zip = createString("zip");

    public QCompanyEntity(String variable) {
        super(CompanyEntity.class, forVariable(variable));
    }

    public QCompanyEntity(Path<? extends CompanyEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QCompanyEntity(PathMetadata<?> metadata) {
        super(CompanyEntity.class, metadata);
    }

}

