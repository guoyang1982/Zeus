package com.gy.sched.common.domain.store;

import lombok.Data;

import java.util.Date;

/**
 * Created by guoyang on 17/5/19.
 */
@Data
public class App {
    Long id;
    String appName;
    String ip;
    Date gmtCreate;
    Date gmtModified;
}
