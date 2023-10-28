package org.niias.asrb.kn.controller;

import org.niias.asrb.kn.model.File;
import org.niias.asrb.kn.repository.FileRepository;
import org.niias.asrb.kn.service.FileService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import java.io.IOException;
import java.util.Base64;

@RestController
public class FileController {

    @Inject
    private FileRepository fileRepo;

    @Inject
    private FileService fileService;

    @Value("${filePath}")
    private String filePath;

    @RequestMapping(value = "/api/file/{id}")
    public ResponseEntity<Resource> download(@PathVariable Integer id) throws IOException {
        File file = fileRepo.findById(id).orElseThrow(IllegalStateException::new);
        byte[] fileContent = fileService.readFile(id.toString());
        if (fileContent == null) {
            fileContent = file.getData();
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + Base64.getEncoder().encodeToString(file.getName().getBytes()))
                .contentLength(fileContent.length)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(new ByteArrayResource(fileContent));

    }


}
