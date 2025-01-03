/*************************************************
Copyright ©, 2017, GY Game
Author: lidudi
Created: 2018年12月26日 下午12:54:36
Note：
************************************************/
package cn.gyyx.aoplog.utils;

import org.joda.time.DateTime;

import java.util.Random;

public class StringTools {

	private static final DateTime EPOCH = new DateTime(2010, 1, 1, 8, 0, 0, 0);
	private static Random rand = new Random();
	
	private StringTools(){}
	
	/**
	 * @param src
	 * @return
	 */
	public static String fillLength(String src) {
        if (src.length() < 3) {
            char[] zeros = new char[3 - src.length()];
            for (int i = 0; i < zeros.length; i++) {
                zeros[i] = '0';
            }
            return new String(zeros) + src;
        } else {
            return src;
        }
    }
	
	public static String getId(String serverIp,String domain){
        return String.format("%s-%s-%s-%s-%s",
        		Hashs.compute(domain),
                StringTools.fillLength(Long.toHexString((DateTime.now().getMillis()-EPOCH.getMillis())*10000)),
                StringTools.fillLength(Long.toHexString(Thread.currentThread().getId())),
                Hashs.compute(serverIp),
                StringTools.fillLength(Integer.toHexString(rand.nextInt(999999))));
    }
}
