package org.niias.asrb.kn.model;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;


/**
 * QSyncNormLog is a Querydsl query type for SyncNormLog
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QSyncNormLog extends EntityPathBase<SyncNormLog> {

    private static final long serialVersionUID = -351058063L;

    public static final QSyncNormLog syncNormLog = new QSyncNormLog("syncNormLog");

    public final StringPath error = createString("error");

    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    public final StringPath logList = createString("logList");

    public final NumberPath<Long> nluId = createNumber("nluId", Long.class);

    public final NumberPath<Long> normPlanId = createNumber("normPlanId", Long.class);

    public QSyncNormLog(String variable) {
        super(SyncNormLog.class, forVariable(variable));
    }

    public QSyncNormLog(Path<? extends SyncNormLog> path) {
        super(path.getType(), path.getMetadata());
    }

    public QSyncNormLog(PathMetadata metadata) {
        super(SyncNormLog.class, metadata);
    }

}

