package com.gy.sched.config;

import com.gy.sched.service.JobManageServer;
import com.gy.sched.service.impl.JobManageServerImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

/**
 * Created by guoyang on 17/9/29.
 */
@Configuration
public class BeanConfig {
    @Bean(initMethod = "getId")
    @Scope("prototype")
    public JobManageServer jobManageServer(){
        return new JobManageServerImpl();
    }
}
