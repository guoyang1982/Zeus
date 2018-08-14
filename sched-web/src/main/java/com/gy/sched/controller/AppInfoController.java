package com.gy.sched.controller;

import com.gy.sched.service.AppInfoServer;
import com.gy.sched.service.JobManageServer;
import com.gy.sched.service.impl.AppInfoServerImpl;
import com.gy.sched.vo.AppInfo;
import com.gy.sched.service.Tlog;
import com.gy.zeus.client.SpringBeanLocator;
import com.gy.zeus.client.SpringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.annotation.Resource;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

/**
 * Created by guoyang on 17/9/15.
 */
@Slf4j
@Controller
public class AppInfoController {
    //private Logger logger = Logger.getLogger(AppInfoController.class);

    @Resource
    AppInfoServer appInfoServer;
    @Resource
    JobManageServer jobManageServer;
    int aa = 12444;

    @RequestMapping({"/", "/appInfo"})
    public String index() {

//        AppInfoServer a = SpringBeanLocator.getInstance().getBean("appInfoServer");
//System.out.println(a.getFlag());

        log.info("info info info");
        log.debug("debug debug debug");
        log.error("eeeeeeeeeeeggggggggggggg??????ggg={},yyyy={}",1111,"ddddfffffff");
        List<AppInfo> appInfos = appInfoServer.getLists();
        boolean b = appInfoServer.getFlag();
        try {
            String s = null;
            aa = 345;
            s.equals("");

        } catch (Exception e) {
            log.error("fail!asdf:",e);

            log.error("fail!asdf={},gu={}",e);

            log.error("fail!asdf={},gu={},exception:",123,"ddddgggg",e);
            log.error("fail!asdf={},gu={}",e,123,"ddddgggg");
            log.error("fail!e={},asdf={},gu={}",e,123,"ddddgggg");

            //return "ddddd";
        }finally {
            System.out.println("print!!!!!!!!");
        }
        Tlog logo = new Tlog();
        logo.getT(appInfos);
        log.debug("debug debug debug");
        log.error("结束,e={}","s");

        log.error("结束,e=eeeeeeee");


        return "appInfo";
    }
    public static void main(String args[]){
        String s = null;
        try{
            s.split("");
        }catch (Exception e){
            String ss = "ad%s";
            e.printStackTrace();
            System.out.println(getStackTrace(e));
        }
    }

    private static String getStackTrace(Throwable t)
    {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        try
        {
            t.printStackTrace(pw);
            return sw.toString();
        }
        finally
        {
            pw.close();
        }
    }

}

