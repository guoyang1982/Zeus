package com.gy.sched.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * Created by guoyang on 17/9/15.
 */
@Controller
public class HistoryManageController {

    @RequestMapping({"/", "/historyManage"})
    public ModelAndView index() {

        return new ModelAndView("historyManage");

    }
}
