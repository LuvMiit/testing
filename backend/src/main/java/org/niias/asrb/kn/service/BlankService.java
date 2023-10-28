package org.niias.asrb.kn.service;

import org.niias.asrb.kn.model.*;
import org.niias.asrb.kn.repository.BlankRepository;
import org.niias.asrb.kn.repository.TemplateRepository;
import org.niias.asrb.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

@Service
public class BlankService {

    @Resource
    private BlankRepository blankRepo;
    @Resource
    private TemplateRepository templateRepo;

    @Transactional
    public Blank save(Blank blank, Long dnchNluId) {
        Template tpl = templateRepo.findByDnchId(dnchNluId).get(0);
        blank.setTemplateId(tpl.getId());
        blank.setPostName(tpl.getPostName());
        tpl.getNorms().stream().map(BlankNorm::new).forEach(blank::addNorm);
        return blankRepo.save(blank);
    }

    public Blank create(Integer templateId, User user, Integer year, String docName)
    {
        Template tpl = templateRepo.findById(templateId).orElseThrow(IllegalStateException::new);
        Blank blank = new Blank();
        blank.setTemplateId(tpl.getId());
        blank.setCreated(new UserActionRef(user));
        blank.setPredId(user.getSubdivisionId());
        blank.setPredName(user.getSubdivision());
        //TODO: Определение головного предприятия, не хватает таблицы vert_sv
        blank.setMainId(123);
        blank.setPostName(tpl.getPostName());
        blank.setYear(year);
        blank.setDocName(docName);

        tpl.getNorms().stream().map(BlankNorm::new).forEach(blank::addNorm);
        //blank.setNorms(tpl.getNorms().stream().map(BlankNorm::new).collect(Collectors.toList()));
        return blankRepo.save(blank);
    }

    @Transactional
    public Blank findById(Integer blankId) {
        final Blank blank = blankRepo.findById(blankId).get();
        Logger logger = LoggerFactory.getLogger(BlankService.class);
        logger.trace("количество нормативов - " + blank.getNorms().size());
        int i = 0;
        for (BlankNorm norm : blank.getNorms()) {
            i += norm.getFiles().size();
        }
        logger.trace("количество файлов - " + i);
        return blank;
    }
}
