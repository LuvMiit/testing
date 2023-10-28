package org.niias.asrb.kn.model;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;


/**
 * QBlankViewMark is a Querydsl query type for BlankViewMark
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QBlankViewMark extends EntityPathBase<BlankViewMark> {

    private static final long serialVersionUID = -410783702L;

    public static final QBlankViewMark blankViewMark = new QBlankViewMark("blankViewMark");

    public final NumberPath<Integer> blank_id = createNumber("blank_id", Integer.class);

    public final StringPath comment = createString("comment");

    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    public final NumberPath<Integer> userId = createNumber("userId", Integer.class);

    public final StringPath userName = createString("userName");

    public final StringPath userPred = createString("userPred");

    public final DatePath<java.time.LocalDate> viewDate = createDate("viewDate", java.time.LocalDate.class);

    public QBlankViewMark(String variable) {
        super(BlankViewMark.class, forVariable(variable));
    }

    public QBlankViewMark(Path<? extends BlankViewMark> path) {
        super(path.getType(), path.getMetadata());
    }

    public QBlankViewMark(PathMetadata metadata) {
        super(BlankViewMark.class, metadata);
    }

}

