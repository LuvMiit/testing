package org.niias.asrb.kn.repository;

import org.niias.asrb.kn.model.SyncReportsLog;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

public interface SyncReportsLogRepository  extends CrudRepository<SyncReportsLog, Integer>, QuerydslPredicateExecutor<SyncReportsLog> {
}
