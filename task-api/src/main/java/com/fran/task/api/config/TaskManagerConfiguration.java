package com.fran.task.api.config;

import com.fran.task.domain.port.TaskManagerFactory;
import org.springframework.beans.factory.config.ServiceLocatorFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TaskManagerConfiguration {

    @Bean
    public ServiceLocatorFactoryBean taskManagerFactory() {
        ServiceLocatorFactoryBean factoryBean = new ServiceLocatorFactoryBean();
        factoryBean.setServiceLocatorInterface(TaskManagerFactory.class);
        return factoryBean;
    }

}
