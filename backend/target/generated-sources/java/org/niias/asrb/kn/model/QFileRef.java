package org.niias.asrb.kn.model;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;


/**
 * QFileRef is a Querydsl query type for FileRef
 */
@Generated("com.querydsl.codegen.EmbeddableSerializer")
public class QFileRef extends BeanPath<FileRef> {

    private static final long serialVersionUID = 1374472219L;

    public static final QFileRef fileRef = new QFileRef("fileRef");

    public final NumberPath<Integer> fileId = createNumber("fileId", Integer.class);

    public final StringPath fileName = createString("fileName");

    public final StringPath fileType = createString("fileType");

    public QFileRef(String variable) {
        super(FileRef.class, forVariable(variable));
    }

    public QFileRef(Path<? extends FileRef> path) {
        super(path.getType(), path.getMetadata());
    }

    public QFileRef(PathMetadata metadata) {
        super(FileRef.class, metadata);
    }

}

