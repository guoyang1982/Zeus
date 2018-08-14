package com.gy.sched.service.impl;

import com.gy.sched.service.AppInfoServer;
import com.gy.sched.vo.AppInfo;
import com.gy.zeus.client.ManagedObjAttribute;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by guoyang on 17/9/29.
 */
@Service("appInfoServer")
public class AppInfoServerImpl implements AppInfoServer {
    @ManagedObjAttribute
    String guo="gggggggggggg";
    @Override
    public List<AppInfo> getLists() {
        List<AppInfo> appInfos = new ArrayList<>();
        System.out.println(guo);
        AppInfo appInfo = new AppInfo();
        appInfo.setName("lms");
        appInfos.add(appInfo);
        return appInfos;
    }

    @Override
    public boolean getFlag() {

        System.out.println("ddddddddd");

        return false;
    }
}

