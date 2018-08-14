package com.gy.sched.common.util;

import com.gy.sched.common.constants.Constants;
import jodd.util.StringUtil;

import java.util.List;
import java.util.Random;

/**
 * IP相关工具
 * @author tianyao.myc
 *
 */
public class RandomUtil implements Constants {

	/** 随机生成数字 */
	public static Random random = new Random();


    public static <T> T getRandomObj(List<T> objList) {
        if(objList == null || objList.size() == 0) {
            return null;
        }
        int index = random.nextInt() % objList.size();
        return objList.get(Math.abs(index));
    }
	
	/**
	 * 获取随机索引
	 * @param objList
	 * @return
	 */
	public static int getRandomIndex(List<? extends Object> objList) {
		if(objList == null || objList.size() == 0) {
            return -1;
        }
		int index = random.nextInt() % objList.size();
		return Math.abs(index);
	}
	
	/**
	 * 从套接字地址获得IP
	 * @param addr
	 * @return
	 */
	public static String getIpFromSocketAddress(final String addr) {
		if(StringUtil.isBlank(addr)) {
			return null;
		}
		String[] splitArray = addr.split(COLON);
		if(splitArray.length != 2) {
			return null;
		}
		return splitArray[0];
	}
	
}
