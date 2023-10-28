package org.niias.asrb.kn.repository;

import org.niias.asrb.kn.model.BlankFile;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface BlankFileRepository extends CrudRepository<BlankFile, Integer> {
    List<BlankFile> findByDnchId(Long dnchId);
}
