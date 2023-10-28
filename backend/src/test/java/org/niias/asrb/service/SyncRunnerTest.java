package org.niias.asrb.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.niias.asrb.kn.model.BlankFile;
import org.niias.asrb.kn.model.FileRef;
import org.niias.asrb.kn.model.SyncReportLog;
import org.niias.asrb.kn.model.SyncReportsLog;
import org.niias.asrb.kn.repository.SyncReportsLogRepository;
import org.niias.asrb.kn.service.DNCHSyncService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import javax.inject.Inject;
import java.util.*;

import static org.niias.asrb.kn.model.SyncInitiator.TEST_METHOD;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
@ActiveProfiles("vpn")
public class SyncRunnerTest {

    @Autowired
    private DNCHSyncService service;

    @Inject
    private SyncReportsLogRepository syncReportsLogRep;

    @Test
    @Ignore
    public void syncTest() {
        Date date = new Date();
        service.runActiveNormSync(date.getTime() - 24 * 60 * 60 * 1000L, date.getTime(), TEST_METHOD);
    }

    @Test
    @Ignore
    public void syncTest2() {
        SyncReportsLog log = new SyncReportsLog(new Date().getTime() - 3600, new Date().getTime(), TEST_METHOD);
        Map map = new HashMap();
        map.put("key1", "value1");
        map.put("key2", "value2");

        List< BlankFile > blankNorms = new ArrayList<>();
        blankNorms.add(new BlankFile(1, 2, new FileRef(1, "file1", "txt"), 666L));
        blankNorms.add(new BlankFile(11, 22, new FileRef(11, "file2", "txt"), 666L));

        SyncReportLog log1 = SyncReportLog.build(map, 666L, blankNorms);
        log1.setLogList("msg 1 <br/>msg 2 <br/>msg 3 <br/>");
        log1.setFiles(Arrays.asList(map, map));

        SyncReportLog log2 = SyncReportLog.build(map, 777L, Collections.emptyList());
        SyncReportLog log3 = SyncReportLog.buildError(map, "TEST ERROR");
        log.setReportLogs(Arrays.asList(log1, log2, log3));

        log = syncReportsLogRep.save(log);
        SyncReportsLog loaded = syncReportsLogRep.findById(log.getId()).get();
        Assert.assertNotNull(loaded);
    }
    @Test
//    @Ignore
//    можно запросить отчет по id плана, взять его ont_time_write и в интервале задать
//    http://localhost:4200/ajax.php?module=NSIOut&method=send_TRA&s=DNCH&param=
//    {"module":"TRA_Document","methods":"findByIdParent","params":{"idParent":"66740709023263"}}
    public void runActiveReportSyncRunner() {
//        id -> {Long@13176} 60490712114756 idParent -> {Long@13160} 66740709023263 name -> Отчет контроля нормативов
//        SyncReportsLog syncReportsLog = service.runActiveReportSync(1675755781814l, 1675755781816l, SyncInitiator.DEV_DNCH_REPORT_SYNC);

        //еще раз вызвать для этого отчета и узнать почему не создался DnchNormToBlankRef
        //Но другой объект, может не выгореть

//        1.За январь из отчета, созданного к плану в ИСУЖТ НС ДНЧ, вообще ничего не пришло:
//        66740709023263 "Контроль нормативов ДГИ за январь 2023" 1673608341184
//        service.runActiveNormSync(1673608341183l, 1673608341185l);
//        Ссылка для плана с id=66740709023263, бланк id=1828587 сохранена
        //теперь попробуем заполнить ее файлами
        //сюда уже передаются данные отчета - 60490712114756 Отчет контроля нормативов 1675755781815
//        SyncReportsLog syncReportsLog = service.runActiveReportSync(1675755781814l, 1675755781816l, SyncInitiator.DEV_DNCH_REPORT_SYNC);
        //после первой синхронизации файлы были сохранены успешно, дважды они не создаются


//        2.За февраль из п.1.4. не пришёл документ в КН:
//        60550717366445 "Контроль нормативов ДГИ за февраль 2023" 1675759584438
//        бланк (1773374) для него найден, синхронизации не будет
//        service.runActiveNormSync(1675759584437l, 1675759584439l);
        //теперь попробуем заполнить ее файлами
        //вот его отчет - 69090704182859 Отчет контроля нормативов 1677843843682
//        SyncReportsLog syncReportsLog = service.runActiveReportSync(1677843843681l, 1677843843683l, SyncInitiator.DEV_DNCH_REPORT_SYNC);

        //почему в разные бланки ушли
//СНАЧАЛА ПЛАН ПОТОМ ОТЧЕТ

        //отчет для 58990710247008 (1672239053152) - 59900712568838 1675685841755
//        service.runActiveNormSync(1674827552271l, 1674827552273l);
//        SyncReportsLog syncReportsLog = service.runActiveReportSync(1677735604609l, 1677735604629l, SyncInitiator.DEV_DNCH_REPORT_SYNC);

//        SyncReportsLog syncReportsLog = service.runActiveReportSync(1677843843672l, 1677843843692l, SyncInitiator.DEV_DNCH_REPORT_SYNC);



//        System.out.println(syncReportsLog);
    }

}
