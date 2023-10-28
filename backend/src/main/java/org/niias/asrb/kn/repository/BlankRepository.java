package org.niias.asrb.kn.repository;

import org.niias.asrb.kn.model.Blank;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;

public interface BlankRepository extends CrudRepository<Blank, Integer>, QuerydslPredicateExecutor<Blank> {
}
