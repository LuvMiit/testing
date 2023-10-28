package org.niias.asrb.kn.repository;

import org.niias.asrb.kn.model.Status;
import org.niias.asrb.kn.model.Template;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDate;
import java.util.List;

public interface TemplateRepository extends CrudRepository<Template, Integer> {
    List<Template> findByDnchId(Long id);


    List<Template> findByStatusAndToDateLessThan(Status status, LocalDate to);
}
