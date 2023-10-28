package org.niias.asrb.kn.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ApiController {

    @RequestMapping("/api/version")
    String version(){
        return "0.0.1";
    }

}
