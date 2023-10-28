package org.niias.asrb.kn.service;

import org.niias.asrb.kn.model.Status;
import org.niias.asrb.kn.model.Template;
import org.niias.asrb.kn.repository.TemplateRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.List;

@Service
public class TempateCheckOutdateService {
    @Resource
    private TemplateRepository teplateRepo;

    @Resource
    private JobPrimaryCheckerService checkerService;

    @Scheduled(cron = "43 7 0 * * ?")
    public void checkScheduled() {
        final Logger logger = LoggerFactory.getLogger(getClass());
        if (checkerService.shouldStartJob()) {
            try {
                logger.info("Запускаем проверку на устаревшие шаблоны");
                final List<Template> outdated = teplateRepo.findByStatusAndToDateLessThan(Status.approved, LocalDate.now().plusDays(1));
                for (Template template : outdated) {
                    template.setStatus(Status.outdated);
                    logger.info("Переводим шаблон " + template.getPostName() + "(" + template.getId() + ") в устаревшие, так как у него подошел срок - " + template.getToDate());
                }
                teplateRepo.saveAll(outdated);
                logger.info("Проверка на устаревшие шаблоны завершена");
            } catch (Exception e) {
                logger.error("Ошибка проверки шаблонов предназначенных для перевода в устаревшие", e);
            }
        }
    }
}
