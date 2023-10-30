package org.niias.asrb.kn.repository;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.niias.asrb.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertNotNull;
@DataJpaTest
@RunWith(SpringRunner.class)
@TestPropertySource("classpath:/application-test.properties")
public class UserRepositoryTest {

    @Autowired
    UserRepository userRepository;
    
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