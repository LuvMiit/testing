package org.niias.asrb.kn.repository;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.niias.asrb.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.*;
@DataJpaTest
@RunWith(SpringJUnit4ClassRunner.class)
@TestPropertySource("/application-test.properties")
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TestEntityManager testEntityManager;

    @Test
    public void testCreate(){
        User user = new User();
        user.setLogin("Morozlogin");
        user.setSubdivisionId(2);
        userRepository.save(user);
        assertNotNull(user);
    }
    @Test
    public void testByLogin(){
        User user = testEntityManager.persist(new User());

    }

}