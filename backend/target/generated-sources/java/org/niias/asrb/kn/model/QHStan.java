package org.niias.asrb.kn.model;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;


/**
 * QHStan is a Querydsl query type for HStan
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QHStan extends EntityPathBase<HStan> {

    private static final long serialVersionUID = 722181754L;

    public static final QHStan hStan = new QHStan("hStan");

    public final StringPath corTip = createString("corTip");

    public final DateTimePath<java.util.Date> dateKd = createDateTime("dateKd", java.util.Date.class);

    public final DateTimePath<java.util.Date> dateNd = createDateTime("dateNd", java.util.Date.class);

    public final NumberPath<Integer> dorKod = createNumber("dorKod", Integer.class);

    public final StringPath name = createString("name");

    public final NumberPath<Integer> stanId = createNumber("stanId", Integer.class);

    public QHStan(String variable) {
        super(HStan.class, forVariable(variable));
    }

    public QHStan(Path<? extends HStan> path) {
        super(path.getType(), path.getMetadata());
    }

    public QHStan(PathMetadata metadata) {
        super(HStan.class, metadata);
    }

}

