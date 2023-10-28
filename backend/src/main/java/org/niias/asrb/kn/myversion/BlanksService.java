package org.niias.asrb.kn.myversion;

import org.modelmapper.ModelMapper;
import org.niias.asrb.kn.repository.UserRepository;
import org.niias.asrb.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class BlanksService {

    private final ModelMapper modelMapper;
    private final UserRepository uRepo;
    private final BlanksDAO bvDAO;

    @Autowired
    public BlanksService(ModelMapper modelMapper, UserRepository uRepo, BlanksDAO bvDAO){
        this.modelMapper = modelMapper;
        this.uRepo=uRepo;
        this.bvDAO=bvDAO;
    }
    public List<UsersBlanksDTO> getReport(){
        List<UsersBlanksDTO> ubDTO = new ArrayList<>();

        for (BlanksViewsDTO bvDTO: bvDAO.getDTOFromSQL()) {
            User user = uRepo.findById(bvDTO.getCreatedUserId()).orElse(null);
            UsersBlanksDTO bUserDTO = new UsersBlanksDTO();
            if (user != null)
                bUserDTO = enreachDTO(bvDTO, user);
            else continue;
            ubDTO.add(bUserDTO);
        }
        return ubDTO;
    }
    private UsersBlanksDTO enreachDTO(BlanksViewsDTO bvDTO, User user){
        UsersBlanksDTO buDTO = modelMapper.map(bvDTO, UsersBlanksDTO.class);
        buDTO.setRailwayShortName(user.getRailroad().getShortName());
        buDTO.setFio(user.getFio());
        buDTO.setSubdivision(user.getSubdivision());

        return buDTO;
    }

}
