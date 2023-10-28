package org.niias.asrb.kn.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
public class SPAController {

    @RequestMapping(value = "/**/{path:[^\\.]*}")
    public ModelAndView redirect() {
        return new ModelAndView("forward:/index.html");
    }

}
