package com.city.support.regime.collection.util;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
 * 自动清除冗余文件线程类
 * @author chenyongqiang
 *
 */
public class AutoCleanRedundanceFilesThread extends Thread {
    @Override
    public void run() {
        long begin = 0;
        long period = AutoCleanRedundanceFiles.DEFAULTTIME * 60;
        do {
            //删除指定的文件（手动）
            if (begin != 0) {
                for (int i = AutoCleanRedundanceFiles.redundanceFiles.size() - 1; i >= 0 ; i--) {
                    String filePath = AutoCleanRedundanceFiles.redundanceFiles.get(i);
                    long time = AutoCleanRedundanceFiles.recordAddTimeMap.get(filePath);
                    if ((time + period * 1000) < System.currentTimeMillis()) {//如果当前时间已经超过设定删除文件时间点，则删除文件
                        File file = new File(filePath);
                        String fileName = file.getName();
                        if (file.exists()) {
                            boolean flag = AutoCleanRedundanceFiles.deleteDir(file);
                            if (flag) {//删除
                                AutoCleanRedundanceFiles.recordAddTimeMap.remove(filePath);
                                AutoCleanRedundanceFiles.redundanceFiles.remove(i);
                            }
                        }
                    }
                }
                //删除没指定的文件（自动）
                for (int i = 0; i < AutoCleanRedundanceFiles.redundanceLocalDir.length; i++) {
                    File dir = new File(AutoCleanRedundanceFiles.redundanceLocalDir[i]);
                    if (dir.exists() && dir.isDirectory()) {
                        File files[] = dir.listFiles();
                        for (int j = files.length - 1; j >= 0; j--) {
                            if (files[j].isDirectory()) {
                                //匹配需要删除的文件夹
                                String dirName = files[j].getName();
                                Pattern pattern = Pattern.compile(".*(_\\d+){6}");//文件夹格式如 ： 0文本文件测试_2013_01_10_09_30_56
                                Matcher matcher = pattern.matcher(dirName);
                                if (matcher.matches()) {
                                    AutoCleanRedundanceFiles.deleteDir(files[j]);
                                }
                            }
                        }
                    }
                }
            }
            begin = System.currentTimeMillis();
            try {
                Thread.sleep(period * 1000);//暂停默认时间
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } while (begin + period < System.currentTimeMillis());//其实加个true就可以，这样是为了双保险吧。
    }
}