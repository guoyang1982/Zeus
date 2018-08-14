package com.gy.sched.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class IndexController {

    @RequestMapping({"/", "/index"})
    public ModelAndView index() {

        return new ModelAndView("index");

    }

    @RequestMapping({"/", "/index_v1"})
    public ModelAndView indexV1() {

        return new ModelAndView("index_v1");
    }
}
