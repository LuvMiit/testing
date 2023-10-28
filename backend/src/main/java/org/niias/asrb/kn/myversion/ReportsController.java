package org.niias.asrb.kn.myversion;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import java.util.List;

@RestController
public class ReportsController {
    @Inject
    BlanksService blanksService;
    @RequestMapping(value="/api/users")
    public List<UsersBlanksDTO> getReport(){
        return blanksService.getReport();
    }
}
