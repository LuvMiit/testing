package org.niias.asrb.kn.model;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QSyncReportsLog is a Querydsl query type for SyncReportsLog
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QSyncReportsLog extends EntityPathBase<SyncReportsLog> {

    private static final long serialVersionUID = -634421540L;

    public static final QSyncReportsLog syncReportsLog = new QSyncReportsLog("syncReportsLog");

    public final DateTimePath<java.util.Date> endDate = createDateTime("endDate", java.util.Date.class);

    public final StringPath error = createString("error");

    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    public final StringPath initiator = createString("initiator");

    public final DateTimePath<java.util.Date> processDate = createDateTime("processDate", java.util.Date.class);

    public final ListPath<SyncReportLog, QSyncReportLog> reportLogs = this.<SyncReportLog, QSyncReportLog>createList("reportLogs", SyncReportLog.class, QSyncReportLog.class, PathInits.DIRECT2);

    public final DateTimePath<java.util.Date> startDate = createDateTime("startDate", java.util.Date.class);

    public QSyncReportsLog(String variable) {
        super(SyncReportsLog.class, forVariable(variable));
    }

    public QSyncReportsLog(Path<? extends SyncReportsLog> path) {
        super(path.getType(), path.getMetadata());
    }

    public QSyncReportsLog(PathMetadata metadata) {
        super(SyncReportsLog.class, metadata);
    }

}

