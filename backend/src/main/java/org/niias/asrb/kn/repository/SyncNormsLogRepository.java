package org.niias.asrb.kn.repository;

import org.niias.asrb.kn.model.SyncNormsLog;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

public interface SyncNormsLogRepository extends CrudRepository<SyncNormsLog, Integer>, QuerydslPredicateExecutor<SyncNormsLog> {
}
