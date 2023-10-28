package org.niias.asrb.kn.model;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QBlank is a Querydsl query type for Blank
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QBlank extends EntityPathBase<Blank> {

    private static final long serialVersionUID = 717367544L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QBlank blank = new QBlank("blank");

    public final QUserActionRef created;

    public final StringPath docName = createString("docName");

    public final NumberPath<Integer> dorKod = createNumber("dorKod", Integer.class);

    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    public final EnumPath<PredLevel> level = createEnum("level", PredLevel.class);

    public final NumberPath<Integer> mainId = createNumber("mainId", Integer.class);

    public final ListPath<BlankNorm, QBlankNorm> norms = this.<BlankNorm, QBlankNorm>createList("norms", BlankNorm.class, QBlankNorm.class, PathInits.DIRECT2);

    public final SimplePath<CompletionMark> open = createSimple("open", CompletionMark.class);

    public final StringPath postName = createString("postName");

    public final NumberPath<Integer> predId = createNumber("predId", Integer.class);

    public final StringPath predName = createString("predName");

    public final NumberPath<Integer> regId = createNumber("regId", Integer.class);

    public final NumberPath<Integer> templateId = createNumber("templateId", Integer.class);

    public final ListPath<BlankViewMark, QBlankViewMark> views = this.<BlankViewMark, QBlankViewMark>createList("views", BlankViewMark.class, QBlankViewMark.class, PathInits.DIRECT2);

    public final NumberPath<Integer> year = createNumber("year", Integer.class);

    public QBlank(String variable) {
        this(Blank.class, forVariable(variable), INITS);
    }

    public QBlank(Path<? extends Blank> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QBlank(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QBlank(PathMetadata metadata, PathInits inits) {
        this(Blank.class, metadata, inits);
    }

    public QBlank(Class<? extends Blank> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.created = inits.isInitialized("created") ? new QUserActionRef(forProperty("created")) : null;
    }

}

