package org.niias.asrb.kn.model;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;


/**
 * QDnchNormToBlankRef is a Querydsl query type for DnchNormToBlankRef
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QDnchNormToBlankRef extends EntityPathBase<DnchNormToBlankRef> {

    private static final long serialVersionUID = 121013825L;

    public static final QDnchNormToBlankRef dnchNormToBlankRef = new QDnchNormToBlankRef("dnchNormToBlankRef");

    public final NumberPath<Integer> blankId = createNumber("blankId", Integer.class);

    public final NumberPath<Long> dnchId = createNumber("dnchId", Long.class);

    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    public final NumberPath<Integer> month = createNumber("month", Integer.class);

    public QDnchNormToBlankRef(String variable) {
        super(DnchNormToBlankRef.class, forVariable(variable));
    }

    public QDnchNormToBlankRef(Path<? extends DnchNormToBlankRef> path) {
        super(path.getType(), path.getMetadata());
    }

    public QDnchNormToBlankRef(PathMetadata metadata) {
        super(DnchNormToBlankRef.class, metadata);
    }

}

