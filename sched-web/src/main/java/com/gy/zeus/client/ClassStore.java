package com.gy.zeus.client;

import java.util.ArrayList;
import java.util.List;

/**
 * @author guoyang
 * @Description: TODO
 * @date 2018/3/24 下午11:10
 */
public class ClassStore {
    private static List<Class> list = new ArrayList<>();
    public static void setList(Class clazz){
        list.add(clazz);
    }

    public static List<Class> getList(){
        return list;
    }
}
