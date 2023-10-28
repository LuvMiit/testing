package org.niias.asrb.kn.controller;

import org.niias.asrb.kn.model.Template;
import org.niias.asrb.model.User;
import org.niias.asrb.kn.model.UserActionRef;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;

@RestController
@RequestMapping
public class TestController {

    @Inject
    private User user;


    @PostMapping("/api/some")
    public Template some(){
        Template  tpl =  new Template();
        tpl.setCreated(new UserActionRef(user));
        return tpl;
    }


}
