package com.ureca.juksoon.domain.feed.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QFeedFile is a Querydsl query type for FeedFile
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QFeedFile extends EntityPathBase<FeedFile> {

    private static final long serialVersionUID = 161537175L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QFeedFile feedFile = new QFeedFile("feedFile");

    public final com.ureca.juksoon.domain.common.QBaseEntity _super = new com.ureca.juksoon.domain.common.QBaseEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final QFeed feed;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifiedAt = _super.modifiedAt;

    public final EnumPath<FileType> type = createEnum("type", FileType.class);

    public final StringPath url = createString("url");

    public QFeedFile(String variable) {
        this(FeedFile.class, forVariable(variable), INITS);
    }

    public QFeedFile(Path<? extends FeedFile> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QFeedFile(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QFeedFile(PathMetadata metadata, PathInits inits) {
        this(FeedFile.class, metadata, inits);
    }

    public QFeedFile(Class<? extends FeedFile> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.feed = inits.isInitialized("feed") ? new QFeed(forProperty("feed"), inits.get("feed")) : null;
    }

}

