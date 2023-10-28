package org.niias.asrb.kn.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mysema.commons.lang.URLEncoder;
import org.apache.commons.collections4.CollectionUtils;
import org.niias.asrb.kn.model.*;
import org.niias.asrb.kn.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import javax.inject.Inject;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.sql.Timestamp;
import java.util.*;

@Service
public class DNCHSyncService {

    @Inject
    TemplateRepository templateRepository;

    @Inject
    private BlankRepository blankRepository;

    @Inject
    private VerticalService verticalService;

    @Inject
    private FileRepository fileRepo;

    @Inject
    private BlankNormRepository normRepo;

    @Inject
    private BlankFileRepository blankFileRepository;

    @Inject
    DnchNormToBlankRefRepository dnchNormToBlankRefRepository;

    @Inject
    private BlankService blankService;

    @Value("${isuztHost:svrw-isugt-wb.svrw.oao.rzd:8094}")
    private String isuztAddress;

    @Value("${isuztUser}")
    private String isuztUser;

    @Value("${isuztPwd}")
    private String isuztPwd;

    private String isuztKey;

    @Value("${isuztSyncEnabled:false}")
    private boolean isuztSyncEnabled;

    @Resource
    private JobPrimaryCheckerService checkerService;

    @Inject
    private BlankRepository blankRepo;

    @Inject
    private FileService fileService;

    @Value("${filePath}")
    private String filePath;

    @Inject
    private SyncReportsLogRepository syncReportsLogRep;

    @Inject
    private SyncNormsLogRepository syncNormsLogRepository;

    Logger logger = LoggerFactory.getLogger(DNCHSyncService.class);

    public void removeBlank(Long idBlank) {
        List<DnchNormToBlankRef> dnchRefList = dnchNormToBlankRefRepository.findByBlankId(idBlank.intValue());
        Blank blank = blankRepo.findById(idBlank.intValue()).get();
        blankRepo.delete(blank);
        Iterator<DnchNormToBlankRef> it = dnchRefList.iterator();
        while (it.hasNext()){
            DnchNormToBlankRef ref = it.next();
            dnchNormToBlankRefRepository.delete(ref);
        }
    }

    public Map getTraDocument(Long id){
        final Map res = fetch("module=NSIOut&method=send_TRA&s=DNCH&param={\"module\":\"TRA_Document\",\"methods\":\"findById\",\"params\":{\"id\":["+ id +"]}}");
        final List<Map> items = res == null || res.get("data") == null ? Collections.emptyList() : (List) res.get("data");
        return items.get(0);
    }

    private List<Map> getTraDocumentByParentId(Long idParent) {
        final Map res = fetch("module=NSIOut&method=send_TRA&s=DNCH&param={\"module\":\"TRA_Document\",\"methods\":\"findByIdParent\",\"params\":{\"idParent\":"+ idParent +"}}");
        return res == null || res.get("data") == null ? Collections.emptyList() : (List) res.get("data");
    }

    public SyncReportLog syncOneReport(Map item)  {//item это отчет контроля нормативов
        final Long reportId = (Long) item.get("id");
        final List<BlankFile> blankNorms = blankFileRepository.findByDnchId(reportId);
        SyncReportLog syncInfo = SyncReportLog.build(item, reportId, blankNorms);
        String message = "Обрабатываем отчет с id=" + reportId;
        logger.info(message);
        syncInfo.addToLogList(message);
        logger.info(item.toString());
        if (blankNorms.isEmpty()) {
            final Long dnchNormPlanId = (Long) item.get("idParent");
            List<DnchNormToBlankRef> blankRefList = dnchNormToBlankRefRepository.findByDnchId(dnchNormPlanId);
            if (blankRefList.isEmpty())
            {
                message = "Ошибка. Не найден контроль нормативов с id плана " + dnchNormPlanId + ". Пропускаем обработку отчета с id " + reportId;
                syncInfo.addToLogList(message);
                logger.error(message);
                return syncInfo;
            }

            DnchNormToBlankRef blankRef = blankRefList.get(0);
            Blank blank = blankService.findById(blankRef.getBlankId());

            final Map structRes = fetch("module=NSIOut&method=send_TRA&s=DNCH&param={\"module\":\"TRA_DocumentStructure\",\"methods\":\"findByIdDocument\",\"params\":{\"idDocument\":" + dnchNormPlanId + "}}");
            final List<Map> struct = structRes == null || structRes.get("data") == null ? Collections.emptyList() : (List) structRes.get("data");
            Map<Long, Long> tplNormIdByStructId = new HashMap<>();

            for (Map map : struct) {
                final Number nluNumberFromProps = getNluNumberFromProps(map, 33440);
                if (nluNumberFromProps != null) {
                    tplNormIdByStructId.put((Long) map.get("id"), nluNumberFromProps.longValue());
                    syncInfo.addToLogList(String.format("Значение свойства 33440 = %s", nluNumberFromProps.longValue()));
                }
            }

            String appIds = null;
            final ObjectMapper mapper = new ObjectMapper();
            List parentProps = null;
            try {
                parentProps = mapper.readValue((String) item.get("props"), List.class);
            }catch (JsonProcessingException ex){
                syncInfo.addToLogList(String.format("ОШИБКА, не удалось распарсить props: %s", item.get("props")));
                throw new RuntimeException(ex);
            }
            //appIds это список приложений отчета плана
            appIds = (String) parentProps.stream().filter(p -> new Integer(33340).equals(((Map) p).get("id"))).map(p -> ((Map) p).get("value")).findFirst().orElse(null);
            syncInfo.addToLogList(String.format("Идентификаторы файлов (свойство 33340) = %s", appIds));
            final Map fileRes = fetch("module=NSIOut&method=send_TRA&s=DNCH&param={\"module\":\"TRA_Applications\",\"methods\":\"findById\",\"params\":{\"ids\":" + appIds + "}}");
            final List<Map> files = fileRes == null || fileRes.get("data") == null ? Collections.emptyList() : (List) fileRes.get("data");
            syncInfo.setFiles(files);
            for (Map fileMap : files) {
                File file = new File();
                //
                final String data = (String) fileMap.get("application");
                final String decoded = URLDecoder.decode(data);
                final String[] split = decoded.split(";");
                final byte[] decode = Base64.getDecoder().decode(split[1].substring("base64,".length()));
                file.setData("file moved".getBytes());
                file.setMimeType(split[0].substring("data:".length()));
                file.setName((String) fileMap.get("nameapplication"));

                // находим нужный пункт
                long descriptionapplication = Long.parseLong((String) fileMap.get("descriptionapplication"));
                final Long normDnchId = tplNormIdByStructId.get(descriptionapplication);
                if (normDnchId != null) {
                final BlankNorm norm = blank.getNorms().stream().filter(n -> normDnchId.equals(n.getTemplateNormDnchId())).findFirst().orElse(null);
                if (norm != null) {
                    final File saved = fileRepo.save(file);
                    final FileRef ref = new FileRef(saved.getId(), saved.getName(), saved.getMimeType());
                    fileService.writeFile(saved.getId().toString(), decode);
                    syncInfo.addToLogList(String.format("Сохранение файла (kn.file) с id=%s", saved.getId().toString()));

                    int month = blankRef.getMonth();
                    BlankFile bf = new BlankFile();
                    bf.setMonth(norm.getSetMonthOfPeriod(month) == 0 ? month : norm.getSetMonthOfPeriod(month));

                    bf.setFileRef(ref);
                    bf.setDnchId(reportId);

                    if (norm.getSetMonthOfPeriod(month) == 0)
                        norm.getCompletion().setMonth(month);
                    // Сохранить
                    syncInfo.addToLogList(String.format("Добавлен бланк файла (kn.blank_file) с FileRef.fileId=%s, месяц=%s", ref.getFileId(), bf.getMonth()));
                    norm.getFiles().add(bf);
                    BlankNorm savedNorm = normRepo.save(norm);
                    syncInfo.addToLogList(String.format(
                            "Сохранен бланк номатива (kn.blank_norm) с id=%s, name=%s, period=%s, customPeriod=%s",
                            savedNorm.getId(), savedNorm.getName(), savedNorm.getPeriod().getLabel(), savedNorm.getCustomPeriod()));
                } else {
                    syncInfo.addToLogList(String.format("Для пункта c normDnchId=%s, не найден BlankNorm, сохранение норматива не произошло", normDnchId));
                }
                } else { //normDnchId == null
                    syncInfo.addToLogList(String.format("Для пункта c descriptionapplication=%s, не найден normDnchId, сохранение файлов не произошло", descriptionapplication));
                }
            }
        } else {
            message = String.format("Найдено %s файла, в синхронизации нет необходимости", blankNorms.size());
            logger.info(message);
            syncInfo.setError(message);
        }
        logger.info("Завершили обрабатывать отчет с dnchId=" + reportId);
        return syncInfo;
    }

    @Scheduled(fixedRate = 15 * 60 * 1000, initialDelay =  10* 60 * 1000)
    public void runActiveReportSync(){
        this.runActiveReportSync(getSyncTime(), null, SyncInitiator.SCHEDULER);
    }

    public SyncReportsLog runActiveReportSync(Long startDate, Long endDate, SyncInitiator initiator) {
        if (endDate == null) {
            endDate = new Date().getTime();
        }
        SyncReportsLog syncReportsLog = new SyncReportsLog(startDate, endDate, initiator);
        if (checkerService.shouldStartJob() && Boolean.TRUE.equals(isuztSyncEnabled)) {
            logger.info("Запускается процесс синхронизации отчетов с ИСУЖТ НС ДНЧ");
            try {
                logger.info("Адрес ИСУЖТ НС ДНЧ " + isuztAddress);

                final Map res = fetch("module=NSIOut&method=send_TRA&s=DNCH&param={\"module\":\"TRA_Document\",\"methods\":\"findActiveDnchReport\",\"params\":" +
                        "{\"startDate\":" + startDate  + ", \"endDate\":" + endDate  + "}}");
                final List<Map> items = res == null || res.get("data") == null ? Collections.emptyList() : (List) res.get("data");
                logger.info("Из ИСУЖТ НС ДНЧ получено действующих отчетов: " + items.size());

                for (Map item : items) {
                    try {
                        SyncReportLog activeReportInfo = syncOneReport(item);
                        syncReportsLog.addActiveReportInfo(activeReportInfo);
                    } catch (Exception ex) {
                        logger.error("Ошибка при обработки отчета", ex);
                        syncReportsLog.addActiveReportInfo(SyncReportLog.buildError(item, ex.getMessage()));
                    }
                }
            } catch (Exception ex) {
                syncReportsLog.setError("Ошибка синхронизации отчетов: " + ex.getMessage());
                logger.info("Ошибка синхронизации отчетов с ИСУЖТ НС ДНЧ завершен", ex);
            }
            logger.info("Процесс синхронизации отчетов с ИСУЖТ НС ДНЧ завершен");
            syncReportsLog = syncReportsLogRep.save(syncReportsLog);
        }
        return syncReportsLog;
    }

    private Long  getSyncTime() {
        final Calendar time = Calendar.getInstance();
        time.add(Calendar.DAY_OF_YEAR, -3);
        return time.getTime().getTime();
    }

    public SyncNormLog syncNormPlan(Map normPlan) {
        final Long normPlanId = (Long) normPlan.get("id");
        SyncNormLog log = new SyncNormLog(normPlanId);
        logger.info("Обрабатываем план нормативов с id = " + normPlanId);
        log(log, "План: " + normPlan);
        final List<DnchNormToBlankRef> blankRefs = dnchNormToBlankRefRepository.findByDnchId(normPlanId);
        if (blankRefs.isEmpty()) {
            log(log, "Ссылка (DnchNormToBlankRef) на норматив с id=" + normPlanId + " не найдена. Находим или создаем бланк и создаем для него ссылку. ");
            convertAndSaveBlank(log, normPlan);
        } else {
            log(log, "Норматив с dnchId=" + normPlanId + " уже есть. Пропускаем");
        }
        logger.info("Завершили обрабатывать норматив с dnchId=" + normPlanId);
        return log;
    }

    private void log(SyncNormLog log, String message) {
        logger.info(message);
        log.addToLogList(message);
    }

    @Scheduled(fixedRate = 15 * 60 * 1000, initialDelay =  5* 60 * 1000)
    public void runActiveNormSync() {
        this.runActiveNormSync(getSyncTime(), null, SyncInitiator.SCHEDULER);
    }

    //данная синхронизация получает действующие планы с нлу, у которых ont_time_write в интервале sync-endDate
    //для каждого плана происходит поиск ссылок план->бланк (DnchNormToBlankRef)
    //если ссылка не найдена, то находится или создается бланк и для него создается новая ссылка
    //то есть суть данной синхронизации - создание ссылок DnchNormToBlankRef
    public SyncNormsLog runActiveNormSync(Long sync, Long endDate, SyncInitiator initiator) {
        if (endDate == null) {
            endDate = new Date().getTime();
        }
        SyncNormsLog logs = new SyncNormsLog(sync, endDate, initiator);
        if (checkerService.shouldStartJob() && Boolean.TRUE.equals(isuztSyncEnabled)) {

            logger.info("Запускается процесс синхронизации действующих нормативов с ИСУЖТ НС ДНЧ");
            try {
                logger.info("Адрес ИСУЖТ НС ДНЧ " + isuztAddress);
                final Map res = fetch("module=NSIOut&method=send_TRA&param={\"module\":\"TRA_Document\",\"methods\":\"findActiveNormPlanWithNlu\",\"params\":" +
                        "{\"startDate\":" + sync  + ", \"endDate\":" + endDate  + "}}");
                final List<Map> normPlans = res == null || res.get("data") == null ? Collections.emptyList() : (List) res.get("data");
                logger.info("Из ИСУЖТ НС ДНЧ получено действующих нормативов: " + normPlans.size());
                for (Map normPlan : normPlans) {
                    try {
                        SyncNormLog log = syncNormPlan(normPlan);
                        logs.addLog(log);
                    } catch (Exception ex) {
                        logger.error("Ошибка при обработки норматива", ex);
                    }
                }
            } catch (Exception ex) {
                logger.info("Ошибка синхронизации нормативов с ИСУЖТ НС ДНЧ завершен", ex);
            }
            logger.info("Процесс синхронизации нормативов с ИСУЖТ НС ДНЧ завершен");
            logs = syncNormsLogRepository.save(logs);
        }
        return logs;
    }

    private void convertAndSaveBlank(SyncNormLog log, Map normPlan) {
        final Long normPlanId = (Long) normPlan.get("id");

        final Map nluIdObj = fetchFirstResult("module=NSIOut&method=send_TRA&param={\"module\":\"TRA_Document\",\"methods\":\"findTemplateForNormPlan\",\"params\":{\"idDocument\":" + normPlanId + "}}");
        Long dnchNluId = (Long) nluIdObj.get("nluId");
        log.setNluId(dnchNluId);
        Blank blank = new Blank();

        UserActionRef createdAction = new UserActionRef(-1, (String) normPlan.get("author"));
        setDateCreated(normPlan, createdAction);
        blank.setCreated(createdAction);
        log.addToLogList(createdAction.toString());

        // найти предприятие
        final Number ouId = (Number) normPlan.get("idOrganizationUnit");
        log.addToLogList(String.format("ouId = %s", ouId));
        final Map ou = fetchFirstResult("module=NSIOut&method=send_TRA&param={\"module\":\"TRA_OrganizationUnits\",\"methods\":\"findById\",\"params\":{\"id\":"+ ouId +"}}");
        Integer idLevel = (Integer) ou.get("idlevel");
        PredLevel level = idLevel == 1 ? PredLevel.regional : PredLevel.linear;
        if (idLevel == 2) {
            Number num = getNumberFromProps(ou, 1009);
            final Map parent = fetchFirstResult("module=NSIOut&method=send_TRA&param={\"module\":\"TRA_OrganizationUnits\",\"methods\":\"findById\",\"params\":{\"id\":"+ ou.get("idorganizationunittoplevel") +"}}");
            final Integer propId = 1001;
            Number dorKod = getNumberFromProps(parent, propId);

            HPred pred = verticalService.getDCSPredByDorKodAndNom(dorKod.intValue(), num.intValue());
            List<HPred> uppers = verticalService.getUppers(pred);
            blank.setRegId(uppers.get(0).getId());
            blank.setPredId(pred.getId());
            blank.setPredName(pred.getName());
            blank.setDorKod(dorKod.intValue());

        } else if (idLevel == 3) {
            Number idStan = getNumberFromProps(ou, 1001);
            HPred pred = verticalService.getDSPredByStan(idStan.intValue());
            blank.setPredId(pred.getId());
            blank.setPredName(pred.getName());
            blank.setDorKod(pred.getRailway().getDorKod());
            List<HPred> uppers = verticalService.getUppers(pred);
            // TODO: fix
            HPred upper = uppers.get(0);
            blank.setRegId(upper.getId());

        } else if (idLevel == 1) {
            Number dorKod = getNumberFromProps(ou, 1001);
            HPred pred = verticalService.getDPredByDorKod(dorKod.intValue());
            blank.setPredId(pred.getId());
            blank.setRegId(pred.getId());
            blank.setPredName(pred.getName());
            blank.setDorKod(dorKod.intValue());

        }
        blank.setLevel(level);
        blank.setMainId(CentralPred.CD.getId());

        Number planDate = getNumberFromProps(normPlan, 10040);
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(planDate.longValue());
        log.addToLogList(String.format("Дата плана %s", cal.getTime()));
        blank.setYear(cal.get(Calendar.YEAR));
        blank.setDocName("Экран выполнения нормативов личного участия в обеспечении безопасности движения " + blank.getPredName());
        // проверить что это не другой месяц
        List<Template> tplList = templateRepository.findByDnchId(dnchNluId);
        if (tplList.isEmpty())
        {
            String error = "Не найден шаблон утвержденный шаблон с dnchId=" + dnchNluId + " для документа контроль нормативов id=" + normPlanId + "; Пропускаем обработку этого документа";
            logger.error(error);
            log.setError(error);
            return;
        }

        Template tpl = tplList.get(0);
        final List<Blank> existedBlanks = getExistedBlanks(blank, tpl);
        final Blank createdOrExistedBlank = existedBlanks.isEmpty() ? blankService.save(blank, dnchNluId) : existedBlanks.get(0);
        String message = (existedBlanks.isEmpty() ? "Был создан новый" : "Был использован существующий")+" бланк с id="+createdOrExistedBlank.getId();
        logger.info(message);
        log.addToLogList(message);
        DnchNormToBlankRef ref = new DnchNormToBlankRef();
        ref.setDnchId(normPlanId);
        ref.setMonth(cal.get(Calendar.MONTH) + 1);
        ref.setBlankId(createdOrExistedBlank.getId());
        ref = dnchNormToBlankRefRepository.save(ref);
        message = String.format("Ссылка для плана с id=%s, бланк id=%s, id ссылки=%s сохранена", normPlanId, ref.getBlankId(), ref.getId());
        logger.info(message);
        log.addToLogList(message);
    }

    private List<Blank> getExistedBlanks(Blank blank, Template tpl) {
        Iterable<Blank> iterable = blankRepository.findAll(
                QBlank.blank.level.eq(blank.getLevel()).and(
                        QBlank.blank.mainId.eq(blank.getMainId())).and(
                        QBlank.blank.postName.eq(tpl.getPostName())).and(
                        QBlank.blank.predId.eq(blank.getPredId())).and(
                        QBlank.blank.year.eq(blank.getYear())).and(
                        QBlank.blank.created.name.eq(blank.getCreated().getName())));
        List<Blank> list = new ArrayList<>();
        CollectionUtils.addAll(list, iterable.iterator());
        return list;
    }

    private void setDateCreated(Map item, UserActionRef createdAction) {
        try {
            createdAction.setDate(new Timestamp((Long) item.get("activeReportCreatedDate")).toLocalDateTime().toLocalDate());
        } catch (Exception ignored) {}
    }

    private Number getNumberFromProps(Map parent, Integer propId) {
        try {
            List parentProps = new ObjectMapper().readValue((String) parent.get("props"), List.class);
            Object obj = parentProps.stream().filter(p -> propId.equals(((Map) p).get("id"))).map(p -> ((Map) p).get("value")).findFirst().orElse(null);
            return obj instanceof Number ? ((Number) obj) : Long.parseLong(obj.toString());
        } catch (Exception ex) {
            return null;
        }
    }
    private Number getNluNumberFromProps(Map parent, Integer propId) {
        try {
            final ObjectMapper mapper = new ObjectMapper();
            List parentProps = mapper.readValue((String) parent.get("props"), List.class);
            Object obj = parentProps.stream().filter(p -> propId.equals(((Map) p).get("id"))).map(p -> ((Map) p).get("value")).findFirst().orElse(null);
            return obj instanceof Map ? (Number) ((Map)obj).get("nluNormId") : null;
        } catch (Exception ex) {
            return null;
        }
    }

    private Map fetchFirstResult(String request) {
        final Map ouIdRes = fetch(request);
        final List<Map> ouList = ouIdRes == null || ouIdRes.get("data") == null ? Collections.emptyList() : (List) ouIdRes.get("data");
        return ouList.get(0);
    }

    @Scheduled(fixedRate = 15 * 60 * 1000)
    public void runSync() {
        if (checkerService.shouldStartJob() && Boolean.TRUE.equals(isuztSyncEnabled)) {
            logger.info("Запускается процесс синхронизации шаблонов с ИСУЖТ НС ДНЧ");
            try {
                logger.info("Адрес ИСУЖТ НС ДНЧ " + isuztAddress);
                final Map res = fetch("module=NSIOut&method=send_TRA&s=DNCH&param={\"module\":\"DNCH_NLU_Tepmlate\",\"methods\":\"findNluDocs\",\"params\":{\"status\":[\"approval\"]}}");
                final List<Map> items = res == null || res.get("data") == null ? Collections.emptyList() : (List) res.get("data");
                logger.info("Из ИСУЖТ НС ДНЧ получено шаблонов на утверждении: " + items.size());
                for (Map item : items) {
                    try {
                        final Long dnchId = (Long) item.get("id");
                        logger.info("Обрабатываем шаблон с dnchId=" + dnchId);
                        final Template template = findByDnchId(dnchId);
                        if (template == null) {
                            logger.info("Шаблон с dnchId=" + dnchId + " не найден. Сохраняем");
                            convertAndSave(new Template(), item);
                            logger.info("Шаблон с dnchId=" + dnchId + " сохранен");
                        } else if (!template.getStatus().toString().equals(item.get("status"))) {
                            logger.info("Шаблон с dnchId=" + dnchId + " поменял статус. Обновляем");
                            convertAndSave(template, item);
                            logger.info("Шаблон с dnchId=" + dnchId + " обновлен");
                        } else {
                            logger.info("Шаблон с dnchId=" + dnchId + " не поменял статус. Пропускаем");

                        }
                        logger.info("Завершили обрабатывать шаблон с dnchId=" + dnchId);
                    } catch (Exception ex) {
                        logger.error("Ошибка при обработки шаблона", ex);
                    }
                }
            } catch (Exception ex) {
                logger.info("Ошибка синхронизации шаблонов с ИСУЖТ НС ДНЧ завершен", ex);
            }
            logger.info("Процесс синхронизации шаблонов с ИСУЖТ НС ДНЧ завершен");
        }

    }


    private Map fetch(String request, Map<String, String>... _args) {
        final Map<String, String> arguments = new HashMap<>();
        if (!(_args == null || _args.length == 0)) {
            Arrays.stream(_args).forEach(arguments::putAll);
        }
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.add("Cookie", Optional.ofNullable(isuztKey).orElse("empty"));
            final URI url = new URI("http://" + isuztAddress + "/ajax2.php?" + URLEncoder.encodeURL(request));
            logger.info("запрос: " + "http://" + isuztAddress + "/ajax2.php?" + request);
            ResponseEntity<Map> res = new RestTemplate().exchange(url, HttpMethod.GET, new HttpEntity<>(headers), Map.class);
            return res.getBody();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (HttpClientErrorException.Unauthorized | HttpClientErrorException.Forbidden e) {
            if (arguments.get("withAuth") == null) {
                auth();
                arguments.put("withAuth", "yes");
                fetch(request, arguments);
            } else {
                throw new RuntimeException(e);
            }
        }
        return null;
    }

    private String getSessionCookie(HttpEntity<String> request) {
        final List<String> strings = request.getHeaders().get("Set-Cookie");
        String jSessionID = strings.get(0);
        for (String string : strings) {
            if (string.startsWith("JSESSIONID") || string.startsWith("Bearer"))
                jSessionID = string;
        }
        return jSessionID;
    }

    protected Boolean auth() {
        try {
            final ResponseEntity<String> response = new RestTemplate().exchange("http://" + isuztAddress + "/j_security_check?j_username=" + isuztUser + "&j_password=" + isuztPwd,
                    HttpMethod.GET,
                    new HttpEntity<String>(new HttpHeaders()),
                    String.class);
            if (response.getStatusCode() != HttpStatus.OK) {
                logger.error("error while auth request {}", response.getStatusCode());
                return false;
            }

            HttpEntity<String> request = new HttpEntity<>(response.getHeaders());

            String cookie = getSessionCookie(request);
            return !(isuztKey = cookie).isEmpty();
        } catch (HttpClientErrorException e) {
            logger.error("error while login to isuzt", e);
            return false;
        }
    }

    private void convertAndSave(Template template, Map tplMap) {
        convertToTemplate(template, tplMap);
        Map res = fetch("module=NSIOut&method=send_TRA&s=DNCH&param={\"module\":\"DNCH_NLU_Tepmlate_Norm\",\"methods\":\"findByTemplateId\",\"params\":{\"template_id\":[" + tplMap.get("id") + "],\"order\":\"ix\",\"desc\":true}}");
        final List<Map> items = res == null ? Collections.emptyList() : (List) res.get("data");

        final ArrayList<TemplateNorm> norms = new ArrayList<>();
        for (Map item : items) {
            final TemplateNorm norm = convertToNorm(item);
            norms.add(norm);
        }
        template.setNorms(norms);
        templateRepository.save(template);
//        System.out.println(tpl);

    }


    private void convertToTemplate(Template tpl, Map tplMap) {

        final String status = (String) tplMap.get("status");
        tpl.setStatus("active".equals(status) ? Status.approved : Status.valueOf(status));
        tpl.setMainId(CentralPred.CD.getId());
        tpl.setLevel(PredLevel.valueOf((String) tplMap.get("lvl")));
        tpl.setPostName((String) tplMap.get("post"));
        try {
            final String json = (String) tplMap.get("post_full");
            tpl.setPostFullName((String) new ObjectMapper().readValue(json, Map.class).get("thirdLine"));
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        //private Integer fromRef;
        tpl.setComment((String) tplMap.get("commentary"));
        tpl.setDnchId((Long) tplMap.get("id"));
        tpl.setCreated(new UserActionRef(-1, (String) tplMap.get("created_user_name")));
        if (tplMap.get("created_date") != null && tplMap.get("created_date") != "null")
            tpl.getCreated().setDate(new Timestamp((Long)tplMap.get("created_date")).toLocalDateTime().toLocalDate());
        tpl.setAgreed(new UserActionRef(-1, (String) tplMap.get("agreed_user_name")));
        if (tplMap.get("agreed_date") != null && tplMap.get("agreed_date") != "null")
            tpl.getAgreed().setDate(new Timestamp((Long)tplMap.get("agreed_date")).toLocalDateTime().toLocalDate());

    }

    private TemplateNorm convertToNorm(Map item) {
        final TemplateNorm templateNorm = new TemplateNorm();
        templateNorm.setName((String) item.get("name"));
        templateNorm.setDnchId((Long)item.get("id"));
        final String period = (String) item.get("period");
        if ("necessary".equalsIgnoreCase(period))
            templateNorm.setPeriod(Period.onDemand);
        else
            templateNorm.setPeriod(Period.valueOf(period));
        return templateNorm;
    }


    private Template findByDnchId(Long id) {
        final List<Template> dnchId = templateRepository.findByDnchId(id);
        return dnchId.isEmpty() ? null : dnchId.get(0);

    }

    public void update(Template tpl) {
        logger.info("Запускается процесс обновляние шаблона " + tpl.getDnchId() + " в ИСУЖТ НС ДНЧ");
        try {
            Map<String, Object> entity = new HashMap<>();
            entity.put("id", tpl.getDnchId());
            if (tpl.getApproved() != null) {
                entity.put("approved_user_id", tpl.getApproved().getId());
                entity.put("approved_user_name", tpl.getApproved().getName());
                if (tpl.getApproved().getDate() != null)
                    entity.put("approved_date", Timestamp.valueOf(tpl.getApproved().getDate().atStartOfDay()).getTime());
            }
            entity.put("status", tpl.getStatus() == Status.approved ? "active" : tpl.getStatus().name());
            entity.put("commentary", tpl.getComment());
            if (tpl.getFromDate() != null)
                entity.put("from_date", Timestamp.valueOf(tpl.getFromDate().atStartOfDay()).getTime());
            if (tpl.getToDate() != null)
                entity.put("to_date", Timestamp.valueOf(tpl.getToDate().atStartOfDay()).getTime());
            final HashMap<String, Object> method = new HashMap<>();
            method.put("module", "DNCH_NLU_Tepmlate");
            method.put("methods", "update");
            method.put("params", Collections.singletonMap("entity", entity));
            auth();
            final URI url = new URI("http://" + isuztAddress + "/ajax2.php");

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            headers.set("Cookie", Optional.ofNullable(isuztKey).orElse("empty"));

            MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
            map.add("module", "NSIOut");
            map.add("method", "send_TRA");
            map.add("s", "DNCH");
            map.add("param", new ObjectMapper().writeValueAsString(method));
            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);

            final ResponseEntity<String> exchange = new RestTemplate().exchange(url, HttpMethod.POST,
                    request, String.class);

            logger.info("Ответ сервера ИСУЖТ НС" + exchange.getBody());
        } catch (Exception ex) {
            logger.info("Ошибка синхронизации", ex);
        }
        logger.info("Процесс обновляние шаблона " + tpl.getDnchId() + " с ИСУЖТ НС ДНЧ завершен");

    }


}
