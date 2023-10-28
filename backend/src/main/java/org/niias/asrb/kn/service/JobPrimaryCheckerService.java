package org.niias.asrb.kn.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

@Service
public class JobPrimaryCheckerService {

    @Autowired
    private Environment environment;

    public boolean shouldStartJob() {
        Logger logger = LoggerFactory.getLogger(JobPrimaryCheckerService.class);
        if (isVpnProfile()) {
            logger.info("Синхронизация и другие задачи не выполняется, т.к. spring.profiles.active= " + environment.getActiveProfiles()[0]);
            return false;
        }
        if (environment.getActiveProfiles() != null && environment.getActiveProfiles().length != 0) {
            logger.info("Синхронизация и другие задачи выполняется, т.к. spring.profiles.active= " + environment.getActiveProfiles()[0]);
            return true;
        }

        try {
            Enumeration e = NetworkInterface.getNetworkInterfaces();
            while(e.hasMoreElements())
            {
                NetworkInterface n = (NetworkInterface) e.nextElement();
                Enumeration ee = n.getInetAddresses();
                while (ee.hasMoreElements())
                {
                    InetAddress i = (InetAddress) ee.nextElement();
                    logger.info("Следующий  IP-адрес - " + i.getHostAddress());
                    if (i instanceof Inet4Address && !"127.0.0.1".equals(i.getHostAddress()))
                    {
                        logger.info("Текущий IP - " + i.getHostAddress());
                        final char last = i.getHostAddress().charAt(i.getHostAddress().length() - 1);
                        int num = last - '0';
                        if (num % 2 == 0) {
                            logger.info("Запущен на втором узле - синхронизация и другие задачи не выполняется ");
                            return false;
                        } else {
                            logger.info("Определили узел как primary - запускаем синхронизацию");
                            return true;
                        }
                    }

                }
            }
            logger.info("Не удалось определить адрес - синхронизация и другие задачи не выполняется");
            return false;
        } catch (Exception ex) {
            logger.error("Ошибка при получении IP - синхронизация не выполняется", ex);
            return false;
        }
    }

    private boolean isVpnProfile() {
        return environment.getActiveProfiles() != null && environment.getActiveProfiles().length == 1 && "vpn".equals(environment.getActiveProfiles()[0]);
    }

}
