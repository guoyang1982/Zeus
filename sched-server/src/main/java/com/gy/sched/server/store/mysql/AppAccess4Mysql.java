package com.gy.sched.server.store.mysql;

import com.gy.sched.common.domain.store.App;
import com.gy.sched.common.exception.AccessException;
import com.gy.sched.server.context.ServerContext;
import com.gy.sched.server.store.AppAccess;

import java.util.List;

/**
 * Created by guoyang on 17/5/19.
 */
public class AppAccess4Mysql implements AppAccess, ServerContext{
    @Override
    public long insert(App app) throws AccessException {
        Long result = null;
        try {
            result = (Long)sqlMapClients.getSqlMapClient()
                    .insert("App.insert", app);
        } catch (Throwable e) {
            throw new AccessException("[insert]: error", e);
        }
        if(null == result) {
            return 0L;
        }
        return result;
    }

    @Override
    public List<App> queryAppByName(App appPage) throws AccessException {

        List<App> appList = null;
        try {
            appList = (List<App>) sqlMapClients
                    .getSqlMapClient().queryForList(
                            "App.queryAppByName", appPage);
        } catch (Throwable e) {
            throw new AccessException("[queryAppByName]: error", e);
        }
        return appList;
    }
}
