package com.gy.sched.client.annotation;

import java.lang.annotation.*;

/**
 * Created by guoyang on 17/4/7.
 */
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Scheduled {
    /**
     * 任务时间表达式
     * @return
     */
    String cron() default "";

    /**
     * 任务描述
     * @return
     */
    String description() default "";
}