package org.niias.asrb.kn.controller;


import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ExcelGenerator {

    //Не используется, оставил пока на всякий

    @RequestMapping(value = "/api/get-excel", produces = "application/json")
    public ResponseEntity<byte[]> getDock(@RequestParam String element)  {
        final HttpHeaders httpHeaders= new HttpHeaders();
        //httpHeaders.setContentType(MediaType.APPLICATION);
        byte[] body = element.getBytes();
        String filename = "output.xls";
        httpHeaders.setContentDispositionFormData(filename, filename);

        return new ResponseEntity(body, httpHeaders, HttpStatus.OK);
    }



}
