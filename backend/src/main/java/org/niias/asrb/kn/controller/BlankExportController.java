package org.niias.asrb.kn.controller;

import org.niias.asrb.kn.model.Blank;
import org.niias.asrb.kn.export.*;
import org.niias.asrb.kn.repository.BlankRepository;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.io.IOException;
import java.util.Base64;

@RestController
public class BlankExportController {

    @Inject
    private BlankRepository blankRepo;

    @Inject
    private BlankModelService ms;

    @RequestMapping(value = "/api/export/blank", method = RequestMethod.POST)
    public ResponseEntity export(@RequestParam Integer blankId, @RequestBody BlankModelService.ExportParams params) throws IOException {
        Blank blank = blankRepo.findById(blankId).get();
        BlankExportModel exportModel = ms.createBlankModel(blank, params);
        Render render = Render.getRender(params.getFormat(), exportModel);
        byte [] data = render.render();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + Base64.getEncoder().encodeToString("document".getBytes()))
                .contentLength(data.length)
                .contentType(new MediaType(render.getMime().split("/")[0], render.getMime().split("/")[1]))
                .body(new ByteArrayResource(data));
    }


}
