package org.niias.asrb.kn.repository;

import org.niias.asrb.kn.model.DnchNormToBlankRef;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface DnchNormToBlankRefRepository  extends CrudRepository<DnchNormToBlankRef, Integer> {
    List<DnchNormToBlankRef> findByDnchId(Long dnchId);
    List<DnchNormToBlankRef> findByBlankId(Integer blankId);
}
