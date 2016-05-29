package be.ttime.core.persistence.model;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;


/**
 * QIpAttemptsEntity is a Querydsl query type for IpAttemptsEntity
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QIpAttemptsEntity extends EntityPathBase<IpAttemptsEntity> {

    private static final long serialVersionUID = 2013934299L;

    public static final QIpAttemptsEntity ipAttemptsEntity = new QIpAttemptsEntity("ipAttemptsEntity");

    public final DateTimePath<java.util.Date> date = createDateTime("date", java.util.Date.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath ip = createString("ip");

    public QIpAttemptsEntity(String variable) {
        super(IpAttemptsEntity.class, forVariable(variable));
    }

    public QIpAttemptsEntity(Path<? extends IpAttemptsEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QIpAttemptsEntity(PathMetadata<?> metadata) {
        super(IpAttemptsEntity.class, metadata);
    }

}

