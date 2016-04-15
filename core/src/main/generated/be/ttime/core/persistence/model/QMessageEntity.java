package be.ttime.core.persistence.model;

import static com.mysema.query.types.PathMetadataFactory.*;

import com.mysema.query.types.path.*;

import com.mysema.query.types.PathMetadata;
import javax.annotation.Generated;
import com.mysema.query.types.Path;
import com.mysema.query.types.path.PathInits;


/**
 * QMessageEntity is a Querydsl query type for MessageEntity
 */
@Generated("com.mysema.query.codegen.EntitySerializer")
public class QMessageEntity extends EntityPathBase<MessageEntity> {

    private static final long serialVersionUID = 718021535L;

    public static final QMessageEntity messageEntity = new QMessageEntity("messageEntity");

    public final StringPath domain = createString("domain");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath messageKey = createString("messageKey");

    public final ListPath<MessageTranslationsEntity, QMessageTranslationsEntity> translations = this.<MessageTranslationsEntity, QMessageTranslationsEntity>createList("translations", MessageTranslationsEntity.class, QMessageTranslationsEntity.class, PathInits.DIRECT2);

    public QMessageEntity(String variable) {
        super(MessageEntity.class, forVariable(variable));
    }

    public QMessageEntity(Path<? extends MessageEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QMessageEntity(PathMetadata<?> metadata) {
        super(MessageEntity.class, metadata);
    }

}

