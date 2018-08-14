package com.gy.zeus.client;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import javax.management.MBeanServer;
import java.lang.management.ManagementFactory;
import java.util.List;

/**
 * @author guoyang
 * @Description: TODO
 * @date 2018/3/24 下午8:43
 */
@Component
public class MyApplicationListener implements ApplicationListener<ContextRefreshedEvent> {

    public void onApplicationEvent(ContextRefreshedEvent event) {
        System.out.println("初始化完成！！！！！！！！！！");

        if (event.getApplicationContext().getParent() == null) {
            //do something....
            try {
                final ApplicationContext app = event.getApplicationContext();
                List<Class> list = ClassStore.getList();
                for (Class c : list) {
                    Object o = app.getBean(c);
                    System.out.println("初始化完成" + o);
                    Class clazz = Thread.currentThread().getContextClassLoader().loadClass("com.gy.zeus.agent.ExposureService");
                    Object objectSpring = clazz.getMethod("excute", Class.class,Object.class).invoke(null, c,o);

                }

            } catch (Exception e) {
            }
        }
        System.out.println("初始化完成");
    }
}

