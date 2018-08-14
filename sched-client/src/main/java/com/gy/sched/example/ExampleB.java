package com.gy.sched.example;

import com.gy.sched.client.executor.job.processor.SimpleJobProcessor;
import com.gy.sched.common.domain.result.ProcessResult;
import com.gy.sched.client.annotation.Scheduled;
import com.gy.sched.client.executor.simple.processor.SimpleJobContext;

/**
 * Created by guoyang on 17/3/28.
 */
public class ExampleB implements SimpleJobProcessor {
    @Override
    @Scheduled(cron = "0/5 * * * * ?",description = "zhujie")
    public ProcessResult process(SimpleJobContext context) {
        System.out.println("dddddB"+context.toString());
        ProcessResult s = new ProcessResult(true);
        return s;
    }
}
