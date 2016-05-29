package be.ttime.core.persistence.model;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;


/**
 * QFileTypeEntity is a Querydsl query type for FileTypeEntity
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QFileTypeEntity extends EntityPathBase<FileTypeEntity> {

    private static final long serialVersionUID = 865703972L;

    public static final QFileTypeEntity fileTypeEntity = new QFileTypeEntity("fileTypeEntity");

    public final StringPath name = createString("name");

    public QFileTypeEntity(String variable) {
        super(FileTypeEntity.class, forVariable(variable));
    }

    public QFileTypeEntity(Path<? extends FileTypeEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QFileTypeEntity(PathMetadata<?> metadata) {
        super(FileTypeEntity.class, metadata);
    }

}

