package org.niias.asrb.kn.model;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;


/**
 * QHPred is a Querydsl query type for HPred
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QHPred extends EntityPathBase<HPred> {

    private static final long serialVersionUID = 722090573L;

    public static final QHPred hPred = new QHPred("hPred");

    public final DateTimePath<java.util.Date> corTime = createDateTime("corTime", java.util.Date.class);

    public final StringPath corTip = createString("corTip");

    public final DateTimePath<java.util.Date> dateKd = createDateTime("dateKd", java.util.Date.class);

    public final DateTimePath<java.util.Date> dateNd = createDateTime("dateNd", java.util.Date.class);

    public final NumberPath<Integer> grId = createNumber("grId", Integer.class);

    public final NumberPath<Integer> hid = createNumber("hid", Integer.class);

    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    public final StringPath name = createString("name");

    public final NumberPath<Integer> nom = createNumber("nom", Integer.class);

    public final NumberPath<Integer> okpoKod = createNumber("okpoKod", Integer.class);

    public final StringPath place = createString("place");

    public final EnumPath<Railway> railway = createEnum("railway", Railway.class);

    public final StringPath sname = createString("sname");

    public final NumberPath<Integer> stanId = createNumber("stanId", Integer.class);

    public final NumberPath<Integer> vdId = createNumber("vdId", Integer.class);

    public final StringPath vname = createString("vname");

    public QHPred(String variable) {
        super(HPred.class, forVariable(variable));
    }

    public QHPred(Path<? extends HPred> path) {
        super(path.getType(), path.getMetadata());
    }

    public QHPred(PathMetadata metadata) {
        super(HPred.class, metadata);
    }

}

