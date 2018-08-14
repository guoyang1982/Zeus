package com.gy.sched.example;

import com.gy.sched.client.executor.job.processor.SimpleJobProcessor;
import com.gy.sched.client.executor.simple.processor.SimpleJobContext;
import com.gy.sched.common.domain.result.ProcessResult;

/**
 * Created by guoyang on 17/4/10.
 */
public class ExampleC implements SimpleJobProcessor {
    @Override
    public ProcessResult process(SimpleJobContext context) {
        System.out.println("ccccccccc!!!!!!"+context.toString());
        ProcessResult s = new ProcessResult(true);
        return s;
    }
}
