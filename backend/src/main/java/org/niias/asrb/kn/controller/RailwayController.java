package org.niias.asrb.kn.controller;

import org.niias.asrb.kn.model.Railway;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.stream.Collectors;

@RestController
public class RailwayController {

    @RequestMapping(value = "/api/railways", method = RequestMethod.GET)
    public ResponseEntity getRailways() {
        return new ResponseEntity(
            Arrays.stream(Railway.values()).filter(railway -> railway.getDorKod() != 0).map(railway -> new RailwayValue(railway.getDorKod(), railway.getValue())).collect(Collectors.toList()),
                HttpStatus.OK);
    }

    public static class RailwayValue {
        public Integer value;
        public String name;
        public RailwayValue(Integer dor, String name) {
            this.value = dor;
            this.name = name;
        }
    }
}
