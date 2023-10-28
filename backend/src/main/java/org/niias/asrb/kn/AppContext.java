package org.niias.asrb.kn;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import javax.inject.Named;

@Named
public class AppContext implements ApplicationContextAware {

    public static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext ac) throws BeansException {
        applicationContext = ac;
    }

    public static <T> T getBean(Class<T> beanClass){
        return applicationContext.getBean(beanClass);
    }



}
