package be.ttime.core.persistence.model;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;
import com.mysema.query.types.path.PathInits;


/**
 * QFileEntity is a Querydsl query type for FileEntity
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QFileEntity extends EntityPathBase<FileEntity> {

    private static final long serialVersionUID = 25705418L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QFileEntity fileEntity = new QFileEntity("fileEntity");

    public final QContentDataEntity contentFile;

    public final QFileTypeEntity contentType;

    public final StringPath description = createString("description");

    public final BooleanPath directory = createBoolean("directory");

    public final BooleanPath enabled = createBoolean("enabled");

    public final StringPath extension = createString("extension");

    public final QFileEntity fileParent;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath mimeType = createString("mimeType");

    public final StringPath name = createString("name");

    public final SetPath<PrivilegeEntity, QPrivilegeEntity> privileges = this.<PrivilegeEntity, QPrivilegeEntity>createSet("privileges", PrivilegeEntity.class, QPrivilegeEntity.class, PathInits.DIRECT2);

    public final StringPath serverName = createString("serverName");

    public final NumberPath<Long> size = createNumber("size", Long.class);

    public final ListPath<FileEntity, QFileEntity> taxonomyChildren = this.<FileEntity, QFileEntity>createList("taxonomyChildren", FileEntity.class, QFileEntity.class, PathInits.DIRECT2);

    public final DatePath<java.util.Date> uploadDate = createDate("uploadDate", java.util.Date.class);

    public QFileEntity(String variable) {
        this(FileEntity.class, forVariable(variable), INITS);
    }

    public QFileEntity(Path<? extends FileEntity> path) {
        this(path.getType(), path.getMetadata(), path.getMetadata().isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QFileEntity(PathMetadata<?> metadata) {
        this(metadata, metadata.isRoot() ? INITS : PathInits.DEFAULT);
    }

    public QFileEntity(PathMetadata<?> metadata, PathInits inits) {
        this(FileEntity.class, metadata, inits);
    }

    public QFileEntity(Class<? extends FileEntity> type, PathMetadata<?> metadata, PathInits inits) {
        super(type, metadata, inits);
        this.contentFile = inits.isInitialized("contentFile") ? new QContentDataEntity(forProperty("contentFile"), inits.get("contentFile")) : null;
        this.contentType = inits.isInitialized("contentType") ? new QFileTypeEntity(forProperty("contentType")) : null;
        this.fileParent = inits.isInitialized("fileParent") ? new QFileEntity(forProperty("fileParent"), inits.get("fileParent")) : null;
    }

}

