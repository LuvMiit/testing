package org.niias.asrb.kn.model;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QBlankFile is a Querydsl query type for BlankFile
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QBlankFile extends EntityPathBase<BlankFile> {

    private static final long serialVersionUID = 993416852L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QBlankFile blankFile = new QBlankFile("blankFile");

    public final NumberPath<Long> dnchId = createNumber("dnchId", Long.class);

    public final QFileRef fileRef;

    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    public final NumberPath<Integer> month = createNumber("month", Integer.class);

    public QBlankFile(String variable) {
        this(BlankFile.class, forVariable(variable), INITS);
    }

    public QBlankFile(Path<? extends BlankFile> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QBlankFile(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QBlankFile(PathMetadata metadata, PathInits inits) {
        this(BlankFile.class, metadata, inits);
    }

    public QBlankFile(Class<? extends BlankFile> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.fileRef = inits.isInitialized("fileRef") ? new QFileRef(forProperty("fileRef")) : null;
    }

}

