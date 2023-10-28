package org.niias.asrb.kn.service;

import org.niias.asrb.kn.model.*;
import org.niias.asrb.model.User;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

@Service
public class UserService {

    @Inject
    private VerticalService vs;

    public VerticalDto getVertical(Integer predId){

        VerticalService.VertInfo vi =vs.getVertInfo(predId);
        CentralPred centralPred = CentralPred.findById(vi.getMain().getId());

        VerticalDto vdto = new VerticalDto();
        vdto.mainId = centralPred.getId();
        if (vi.getReg() != null)
            vdto.regId = vi.getReg().getId();
        vdto.mainName = centralPred.getName();
        vdto.vertSize = centralPred.getVertSize();
        vdto.predId = vi.getPred().getId();
        vdto.predName = vi.getPred().getName();
        vdto.level = vi.getLevel();

        if (vdto.regId != null)
        {
            HPred regPred = vs.getPred(vdto.regId);
            vdto.regName = regPred.getName();
        }

        HStan stan = vs.getStan(vdto.predId);
        if (stan != null)
        {
            vdto.dorKod = stan.getDorKod();
            vdto.stanName = stan.getName();
        }

        return vdto;
    }

    public void populateVertical(User user){
        user.getExtras().put("vertical", getVertical(user.getSubdivisionId()));
    }

}
