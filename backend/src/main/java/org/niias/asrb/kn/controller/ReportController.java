package org.niias.asrb.kn.controller;

import org.niias.asrb.kn.export.Render;
import org.niias.asrb.kn.service.ReportService;
import org.niias.asrb.kn.service.ReportService.ReportParam;
import org.niias.asrb.kn.service.ReportService.ReportResult;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.io.IOException;
import java.util.Base64;

@RestController
public class ReportController {

    @Inject
    private ReportService reportService;

    @RequestMapping(value = "/api/report", method = RequestMethod.POST)
    public ReportResult report(@RequestBody ReportParam param){
        return reportService.report(param);
    }

    @RequestMapping(value = "/api/report-export", method = RequestMethod.POST)
    public ResponseEntity report(@RequestBody ReportParam param, @RequestParam String format) throws IOException {
        ReportResult report = reportService.report(param);
        Render render = Render.getRender(format, report);
        byte [] data = render.render();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + Base64.getEncoder().encodeToString("document".getBytes()))
                .contentLength(data.length)
                .contentType(new MediaType(render.getMime().split("/")[0], render.getMime().split("/")[1]))
                .body(new ByteArrayResource(data));
    }

}
