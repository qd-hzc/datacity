package com.city.support.regime.collection.util;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



/**
 * 自动清理冗余的文件夹及其文件
 * @author chenyongqiang
 *
 */
public class AutoCleanRedundanceFiles {

    private static AutoCleanRedundanceFiles autoCleanRedundanceFiles;
    /**
     * 私有的构造函数
     */
    private AutoCleanRedundanceFiles() {

    }
    /**
     * 冗余文件夹所在目录
     */
    public static String redundanceLocalDir[] = {File.separator + "download" + File.separator};
    /**
     * 手动指定定期删除的文件
     */
    public static List<String> redundanceFiles = new ArrayList<String>();
    /**
     * 保存文件存入时的时间
     */
    public static Map<String, Long> recordAddTimeMap = new HashMap<String, Long>();
    /**
     * 默认定期删除时间为15分钟
     */
    public static final int DEFAULTTIME = 15;
    /**
     * 加上绝对路径并启动自动删除功能
     */
    static {
        for (int i = 0; i < redundanceLocalDir.length; i++) {
            redundanceLocalDir[i] = PathUtils.getResourcePath() + redundanceLocalDir[i];
        }
        new AutoCleanRedundanceFilesThread().start();//启动一个线程单独执行清理冗余文件。
    }
    /**
     * 单例模式
     * @return
     */
    public static AutoCleanRedundanceFiles getAutoCleanRedundanceFiles(){
        if (autoCleanRedundanceFiles == null)
            autoCleanRedundanceFiles = new AutoCleanRedundanceFiles();
        return autoCleanRedundanceFiles;
    }
    /**
     * 收集需要定期删除的文件（由于操作用户不多，暂时不需要考虑线程安全问题）
     * @param redundanceFile
     */
    public static void putRedundanceFiles(String redundanceFile){
        if (redundanceFile != null && !"".equals(redundanceFile)) {
            redundanceFiles.add(redundanceFile);
            recordAddTimeMap.put(redundanceFile, System.currentTimeMillis());
        }
    }
    /**
     * 删除目录或文件
     * @author chenyongqiang
     * @param dir
     * @return
     */
    public static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        return dir.delete();
    }


    public static void deleteFolder( File delfolder,String dirName) {
        //  File delfolder=new File(dir);
        File oldFile[] = delfolder.listFiles();
        try
        {
            for (int i = 0; i < oldFile.length; i++)
            {
                if(oldFile[i].isDirectory())
                {
                    deleteFolder(delfolder, dirName+oldFile[i].getName()+"//"); //递归清空子文件夹
                }
                oldFile[i].delete();
            }
        }
        catch (Exception e)
        {
            System.out.println("清空文件夹操作出错!");
            e.printStackTrace();
        }
    }

}
