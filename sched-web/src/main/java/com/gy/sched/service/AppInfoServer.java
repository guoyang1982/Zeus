package com.gy.sched.service;

import com.gy.sched.vo.AppInfo;

import java.util.List;

/**
 * Created by guoyang on 17/9/29.
 */
public interface AppInfoServer {

    public List<AppInfo> getLists();

    public boolean getFlag();
}
