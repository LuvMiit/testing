package org.niias.asrb.kn.model;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;


/**
 * QBlankNormDocument is a Querydsl query type for BlankNormDocument
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QBlankNormDocument extends EntityPathBase<BlankNormDocument> {

    private static final long serialVersionUID = -1475530225L;

    public static final QBlankNormDocument blankNormDocument = new QBlankNormDocument("blankNormDocument");

    public final QBlankNorm _super = new QBlankNorm(this);

    //inherited
    public final SimplePath<CompletionMark> completion = _super.completion;

    //inherited
    public final SimplePath<CompletionMark> customPeriod = _super.customPeriod;

    public final NumberPath<Integer> docId = createNumber("docId", Integer.class);

    //inherited
    public final StringPath docs = _super.docs;

    //inherited
    public final ListPath<BlankFile, QBlankFile> files = _super.files;

    //inherited
    public final NumberPath<Integer> id = _super.id;

    //inherited
    public final StringPath name = _super.name;

    //inherited
    public final EnumPath<Period> period = _super.period;

    public final SimplePath<CompletionMark> plan = createSimple("plan", CompletionMark.class);

    //inherited
    public final NumberPath<Long> templateNormDnchId = _super.templateNormDnchId;

    public QBlankNormDocument(String variable) {
        super(BlankNormDocument.class, forVariable(variable));
    }

    public QBlankNormDocument(Path<? extends BlankNormDocument> path) {
        super(path.getType(), path.getMetadata());
    }

    public QBlankNormDocument(PathMetadata metadata) {
        super(BlankNormDocument.class, metadata);
    }

}

