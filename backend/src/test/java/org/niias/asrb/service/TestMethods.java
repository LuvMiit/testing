package org.niias.asrb.service;

import junit.framework.TestCase;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.niias.asrb.kn.model.UserDTO;
import org.niias.asrb.kn.repository.UserRepository;
import org.niias.asrb.kn.service.UserDAO;
import org.niias.asrb.model.Railway;
import org.niias.asrb.model.User;

import java.util.ArrayList;
import java.util.List;

//@ExtendWith(MockitoExtension.class)
public class TestMethods extends TestCase {
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UserDAO userDAO;

//    @BeforeEach
//    public void createMock(){
//        userRepository = Mockito.mock(UserRepository.class);
//        userDAO = new UserDAO();
//    }
    public void testGetRes(){
        List<User> users = getUsers();
        Mockito.when(userRepository.findAll()).thenReturn(users);
        List <UserDTO> result = userDAO.getRes();

        Assertions.assertNotNull(result);
        Assertions.assertEquals(2, result.size());
        Assertions.assertEquals(users.get(0),result.get(0));
        Assertions.assertEquals(users.get(1),result.get(1));
    }
    private List<User> getUsers(){
        User firstUser = new User();
        User secondUser = new User();

        firstUser.setFio("Иванов Иван Иванович");
        firstUser.setSubdivision("Подразделение1");
        firstUser.setRailroad(Railway.MOSK);

        secondUser.setFio("Петров Петр Петрович");
        secondUser.setSubdivision("Подразделение2");
        secondUser.setRailroad(Railway.DVOST);

        return List.of(firstUser, secondUser);
    }
}
