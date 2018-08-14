package com.gy.sched.service;

import com.gy.sched.service.impl.AppInfoServerImpl;
import com.gy.sched.vo.AppInfo;
import com.gy.zeus.client.SpringUtil;

import java.util.List;

/**
 * @author guoyang
 * @Description: TODO
 * @date 2017/12/1 下午4:30
 */
public class Tlog {
    public String getT(List<AppInfo> s){
        AppInfoServer a = SpringUtil.getBean(AppInfoServerImpl.class);
        System.out.println(a.getFlag());
        System.out.println("TLog TLog TLog!");
        return "gygygy";
    }

    public String getT(){
        System.out.println("TLog TLog TLog!");
        return "gygygy1";
    }

    public String getT1(){
        System.out.println("TLog TLog TLog!");
        return "gygygy2";
    }
}
