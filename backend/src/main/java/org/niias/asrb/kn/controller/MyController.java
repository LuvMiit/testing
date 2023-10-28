package org.niias.asrb.kn.controller;

import org.niias.asrb.kn.model.BlankAndBlankViewDTO;
import org.niias.asrb.kn.service.BlankAndViewDAOService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import java.util.List;

@RestController
public class MyController {
    @Inject
    BlankAndViewDAOService blankAndViewDAOService;

    @GetMapping("/api/testik")
    public List<BlankAndBlankViewDTO> someRes(){
        return blankAndViewDAOService.getBlankRes();

    }
}
