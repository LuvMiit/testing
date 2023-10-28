package org.niias.asrb.kn.controller;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.niias.asrb.kn.model.CentralPred;
import org.niias.asrb.kn.model.PredLevel;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import java.util.List;

import static org.niias.asrb.kn.model.QBlank.blank;

@RestController
public class PostController {

    @Inject
    private JPAQueryFactory qf;

    @RequestMapping(value = "/api/blank/post-search")
    public List<String> getPost(@RequestParam  Integer central, @RequestParam PredLevel level, @RequestParam Integer year){
        if (CentralPred.findById(central) == null)
            throw new IllegalStateException();

        return qf.from(blank)
                .select(blank.postName)
                .where(blank.year.eq(year), blank.mainId.eq(central), blank.level.eq(level))
                .distinct().fetch();
    }

}
