package org.niias.asrb.kn.model;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;


/**
 * QSyncReportLog is a Querydsl query type for SyncReportLog
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QSyncReportLog extends EntityPathBase<SyncReportLog> {

    private static final long serialVersionUID = 1364971705L;

    public static final QSyncReportLog syncReportLog = new QSyncReportLog("syncReportLog");

    public final StringPath blankNorms = createString("blankNorms");

    public final NumberPath<Long> dnchId = createNumber("dnchId", Long.class);

    public final StringPath error = createString("error");

    public final StringPath files = createString("files");

    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    public final StringPath logList = createString("logList");

    public final StringPath reportDocument = createString("reportDocument");

    public QSyncReportLog(String variable) {
        super(SyncReportLog.class, forVariable(variable));
    }

    public QSyncReportLog(Path<? extends SyncReportLog> path) {
        super(path.getType(), path.getMetadata());
    }

    public QSyncReportLog(PathMetadata metadata) {
        super(SyncReportLog.class, metadata);
    }

}

