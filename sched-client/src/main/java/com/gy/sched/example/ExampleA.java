package com.gy.sched.example;

import com.gy.sched.common.domain.result.ProcessResult;
import com.gy.sched.client.api.SimpleTaskApi;
import com.gy.sched.client.executor.job.processor.SimpleJobProcessor;
import com.gy.sched.client.executor.simple.processor.SimpleJobContext;
import com.gy.sched.common.domain.store.Job;
import lombok.Data;

import java.util.Date;

/**
 * Created by guoyang on 17/3/28.
 */
@Data
public class ExampleA implements SimpleJobProcessor {
    SimpleTaskApi simpleTaskApi;
    @Override
    public ProcessResult process(SimpleJobContext context) {
        System.out.println("ddddd"+context.toString());
        Job job = new Job();
        job.setAppName("lms");
        job.setJobProcessor("com.letv.sched.example.ExampleC");
        Date date = new Date();
        long d = date.getTime() + 30000l;
        job.setFireTime(new Date(d));
        job.setDescription("api create!");
        //1为触发
        job.setType(1);
        simpleTaskApi.addTask(job);
        ProcessResult s = new ProcessResult(true);
        return s;
    }
}
