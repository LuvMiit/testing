package org.niias.asrb.kn.controller;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import lombok.Data;
import org.niias.asrb.kn.model.SyncNormsLog;
import org.niias.asrb.kn.model.SyncReportsLog;
import org.niias.asrb.kn.repository.SyncNormsLogRepository;
import org.niias.asrb.kn.repository.SyncReportsLogRepository;
import org.niias.asrb.kn.util.CollectionUtil;
import org.niias.asrb.kn.util.DateUtil;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.Date;
import java.util.List;

import static org.niias.asrb.kn.model.QSyncNormsLog.syncNormsLog;
import static org.niias.asrb.kn.model.QSyncReportsLog.syncReportsLog;

@RestController
public class SyncLogController {

    @Inject
    private SyncReportsLogRepository syncReportsLogRep;

    @Inject
    private SyncNormsLogRepository syncNormsLogRep;

    @Data
    public static class QueryReports {
        Long from;
        Long to;
        Predicate toPredicate() {
            BooleanBuilder bb = new BooleanBuilder();
            bb.and(syncReportsLog.id.isNotNull());
            bb.and(syncReportsLog.processDate.between(DateUtil.startOfDay(new Date(from)), DateUtil.endOfDay(new Date(to))));
            return bb.getValue();
        }
    }

    @Data
    public static class QueryNorms {
        Long from;
        Long to;
        Predicate toPredicate(){
            BooleanBuilder bb = new BooleanBuilder();
            bb.and(syncNormsLog.id.isNotNull());
            bb.and(syncNormsLog.processDate.between(DateUtil.startOfDay(new Date(from)), DateUtil.endOfDay(new Date(to))));
            return bb.getValue();
        }
    }

    @RequestMapping(value = "/api/sync-reports-log/query", method = RequestMethod.POST)
    public List<SyncReportsLog> logs(@RequestBody QueryReports query) {
        return CollectionUtil.toList(syncReportsLogRep.findAll(query.toPredicate()));
    }

    @RequestMapping(value = "/api/sync-norms-log/query", method = RequestMethod.POST)
    public List<SyncNormsLog> logsNorms(@RequestBody QueryNorms query) {
        return CollectionUtil.toList(syncNormsLogRep.findAll(query.toPredicate()));
    }

    @RequestMapping(value = "/api/sync-reports-log/{id}", method = RequestMethod.POST)
    public SyncReportsLog log(@PathVariable int id) {
        SyncReportsLog log = syncReportsLogRep.findById(id).orElse(null);
        if (log != null) {
            log.getReportLogs();
        }
        return log;
    }

    @RequestMapping(value = "/api/sync-norms-log/{id}", method = RequestMethod.POST)
    public SyncNormsLog logNorms(@PathVariable int id) {
        return syncNormsLogRep.findById(id).orElse(null);
    }

}
