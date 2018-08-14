package com.gy.sched.server.store;

import com.gy.sched.common.exception.AccessException;
import com.gy.sched.common.domain.store.App;

import java.util.List;

/**
 * Created by guoyang on 17/5/19.
 */
public interface AppAccess {

    public long insert(App app) throws AccessException;

    public List<App> queryAppByName(App appPage) throws AccessException;
}
