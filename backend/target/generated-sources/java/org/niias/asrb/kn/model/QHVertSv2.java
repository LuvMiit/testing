package org.niias.asrb.kn.model;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;


/**
 * QHVertSv2 is a Querydsl query type for HVertSv2
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QHVertSv2 extends EntityPathBase<HVertSv2> {

    private static final long serialVersionUID = -1020483694L;

    public static final QHVertSv2 hVertSv2 = new QHVertSv2("hVertSv2");

    public final DateTimePath<java.util.Date> corTime = createDateTime("corTime", java.util.Date.class);

    public final StringPath corTip = createString("corTip");

    public final DateTimePath<java.util.Date> dateKd = createDateTime("dateKd", java.util.Date.class);

    public final DateTimePath<java.util.Date> dateNd = createDateTime("dateNd", java.util.Date.class);

    public final NumberPath<Integer> hid = createNumber("hid", Integer.class);

    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    public final NumberPath<Integer> operId = createNumber("operId", Integer.class);

    public final NumberPath<Integer> predNid = createNumber("predNid", Integer.class);

    public final NumberPath<Integer> predVid = createNumber("predVid", Integer.class);

    public final NumberPath<Integer> repl = createNumber("repl", Integer.class);

    public final NumberPath<Integer> vid = createNumber("vid", Integer.class);

    public final NumberPath<Integer> vidPodch = createNumber("vidPodch", Integer.class);

    public QHVertSv2(String variable) {
        super(HVertSv2.class, forVariable(variable));
    }

    public QHVertSv2(Path<? extends HVertSv2> path) {
        super(path.getType(), path.getMetadata());
    }

    public QHVertSv2(PathMetadata metadata) {
        super(HVertSv2.class, metadata);
    }

}

