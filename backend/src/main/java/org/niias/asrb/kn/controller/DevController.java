package org.niias.asrb.kn.controller;

import org.niias.asrb.kn.model.SyncInitiator;
import org.niias.asrb.kn.model.SyncNormsLog;
import org.niias.asrb.kn.model.SyncReportsLog;
import org.niias.asrb.kn.service.DNCHSyncService;
import org.niias.asrb.kn.service.VerticalService;
import org.niias.asrb.kn.util.DateUtil;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

@RestController
@RequestMapping("/dev")
public class DevController {

    @Resource
    private VerticalService vertSrv;

    @Inject
    private DNCHSyncService service;

//    http://localhost:8081/asrb-kn/dev/dnch-report-sync.do?start=01.01.2023&end=31.01.2023
    @RequestMapping("/dnch-report-sync.do")
    public String syncYear(@RequestParam String start, @RequestParam String end) {
        Date startDate = DateUtil.startOfDay(DateUtil.parseDate(start));
        Date endDate = DateUtil.endOfDay(DateUtil.parseDate(end));
        SyncNormsLog normsLog = service.runActiveNormSync(startDate.getTime(), endDate.getTime(), SyncInitiator.DEV_NORM_AND_REPORT_SYNC);
        SyncReportsLog reportsLog = service.runActiveReportSync(startDate.getTime(), endDate.getTime(), SyncInitiator.DEV_NORM_AND_REPORT_SYNC);
        return "ok";
    }

    @RequestMapping("/get-param.do")
    public Map<String, String> getParam(HttpServletRequest request) {
        ServletContext sc = request.getServletContext();
        final Enumeration<String> names = sc.getInitParameterNames();
        final HashMap<String, String> res = new HashMap<>();
        while(names.hasMoreElements()) {
            final String name = names.nextElement();
            res.put(name, sc.getInitParameter("storage"));
        }
        return res;
    }
    @RequestMapping("/sync-dnch.do")
    public String syncDnch() {
        service.runSync();
        return "ok";
    }

    @RequestMapping("/sync-norm-and-report.do")
    public String syncYear(@RequestParam Integer year) {
        final Calendar cal = Calendar.getInstance();
        cal.add(Calendar.YEAR, -year);
        long time = cal.getTime().getTime();
        service.runSync();
        SyncNormsLog normsLog = service.runActiveNormSync(time, null, SyncInitiator.DEV_NORM_AND_REPORT_SYNC);
        SyncReportsLog reportsLog = service.runActiveReportSync(time, null, SyncInitiator.DEV_NORM_AND_REPORT_SYNC);
        return "ok";
    }

    @RequestMapping("/sync-norm-dnch.do")
    public String syncNormDnch() {
        service.runActiveNormSync();
        return "ok";
    }

    @RequestMapping("/sync-report-dnch.do")
    public String syncReportDnch() {
        service.runActiveReportSync();
        return "ok";
    }

    @RequestMapping("/remove-blank.do")
    public String removeBlank(Long idBlank) {
        service.removeBlank(idBlank);
        return "ok";
    }

    @RequestMapping("/desc")
    public String desc(@RequestParam Integer id)  {
        StringBuilder builder =new StringBuilder();
        vertSrv.getDescendants(id).stream().map(it->it.getId() + ", " + it.getName()).forEach(it->builder.append(it).append("\n"));
        return builder.toString();
    }

}
