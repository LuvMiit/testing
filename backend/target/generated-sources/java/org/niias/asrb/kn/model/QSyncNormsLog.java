package org.niias.asrb.kn.model;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QSyncNormsLog is a Querydsl query type for SyncNormsLog
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QSyncNormsLog extends EntityPathBase<SyncNormsLog> {

    private static final long serialVersionUID = 2003230500L;

    public static final QSyncNormsLog syncNormsLog = new QSyncNormsLog("syncNormsLog");

    public final DateTimePath<java.util.Date> endDate = createDateTime("endDate", java.util.Date.class);

    public final StringPath error = createString("error");

    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    public final StringPath initiator = createString("initiator");

    public final ListPath<SyncNormLog, QSyncNormLog> normLogs = this.<SyncNormLog, QSyncNormLog>createList("normLogs", SyncNormLog.class, QSyncNormLog.class, PathInits.DIRECT2);

    public final DateTimePath<java.util.Date> processDate = createDateTime("processDate", java.util.Date.class);

    public final DateTimePath<java.util.Date> startDate = createDateTime("startDate", java.util.Date.class);

    public QSyncNormsLog(String variable) {
        super(SyncNormsLog.class, forVariable(variable));
    }

    public QSyncNormsLog(Path<? extends SyncNormsLog> path) {
        super(path.getType(), path.getMetadata());
    }

    public QSyncNormsLog(PathMetadata metadata) {
        super(SyncNormsLog.class, metadata);
    }

}

