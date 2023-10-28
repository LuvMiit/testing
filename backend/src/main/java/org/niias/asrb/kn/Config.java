package org.niias.asrb.kn;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.modelmapper.ModelMapper;
import org.niias.asrb.kn.model.UserKn;
import org.niias.asrb.kn.model.UserKnImpl;
import org.niias.asrb.model.User;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.annotation.RequestScope;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Configuration
@EnableScheduling
@PropertySources(value = {@PropertySource("classpath:application-dev.properties")})
public class Config {

    @PersistenceContext
    private EntityManager em;

    @Bean
    public ModelMapper modelMapper(){
        return new ModelMapper();
    };
    @Bean
    public JPAQueryFactory getJpaQueryFactory(){
        return new JPAQueryFactory(em);
    }

    @Bean
    @RequestScope
    public User getUser(){
        if (SecurityContextHolder.getContext().getAuthentication() == null)
            return null;
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    @Bean
    @RequestScope
    public UserKn getUserKn(User user){
        return new UserKnImpl(user);
    }


}
