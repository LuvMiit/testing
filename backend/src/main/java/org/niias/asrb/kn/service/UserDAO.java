package org.niias.asrb.kn.service;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.niias.asrb.kn.model.UserDTO;
import org.niias.asrb.kn.repository.UserRepository;
import org.niias.asrb.model.Railway;
import org.niias.asrb.model.User;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserDAO {
    @Inject
    JPAQueryFactory qf;
    @Inject
    UserRepository userRepository;
    public List<UserDTO> getRes(){


       List<User> users = new ArrayList<User>();
       userRepository.findAll().forEach(users::add);
       List<UserDTO> userDTOList= new ArrayList<>(users.size());
       for (User t:users){
           String name=t.getFio();
           String div = t.getSubdivision();
           String rail = t.getRailroad().getShortName();
           userDTOList.add(new UserDTO(name, div, rail));
       }
       return userDTOList;
}

}
