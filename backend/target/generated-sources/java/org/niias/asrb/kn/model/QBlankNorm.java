package org.niias.asrb.kn.model;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QBlankNorm is a Querydsl query type for BlankNorm
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QBlankNorm extends EntityPathBase<BlankNorm> {

    private static final long serialVersionUID = 993661140L;

    public static final QBlankNorm blankNorm = new QBlankNorm("blankNorm");

    public final SimplePath<CompletionMark> completion = createSimple("completion", CompletionMark.class);

    public final SimplePath<CompletionMark> customPeriod = createSimple("customPeriod", CompletionMark.class);

    public final StringPath docs = createString("docs");

    public final ListPath<BlankFile, QBlankFile> files = this.<BlankFile, QBlankFile>createList("files", BlankFile.class, QBlankFile.class, PathInits.DIRECT2);

    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    public final StringPath name = createString("name");

    public final EnumPath<Period> period = createEnum("period", Period.class);

    public final NumberPath<Long> templateNormDnchId = createNumber("templateNormDnchId", Long.class);

    public QBlankNorm(String variable) {
        super(BlankNorm.class, forVariable(variable));
    }

    public QBlankNorm(Path<? extends BlankNorm> path) {
        super(path.getType(), path.getMetadata());
    }

    public QBlankNorm(PathMetadata metadata) {
        super(BlankNorm.class, metadata);
    }

}

