package com.city.support.regime.collection.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipOutputStream;

/**
 * 文件批量导出工具
 *
 * @author chenyongqiang
 */
public class ExportUtils {
    /**
     * 文件批量导出
     *
     * @param response  响应体
     * @param filePaths 文件路径的集合
     * @param zipType   导出类型：single和multi
     * @param path      工程的绝对路径
     */
    public static void export(HttpServletResponse response, List<String> filePaths, String zipType, String path) {
        export(response, filePaths, zipType, path, null);
    }

    /**
     * 文件批量导出
     *
     * @param response    响应体
     * @param filePaths   文件路径的集合
     * @param zipType     导出类型：single和multi
     * @param path        工程的绝对路径
     * @param outFileName 输出文件名
     */
    public static void export(HttpServletResponse response, List<String> filePaths, String zipType, String path, String outFileName) {
        //多报表下载，以zip形式打包下载
        //一共采用两种方式打包，第一种是以文件夹的形式打包，另一种是以多文件的形式打包
        //第一种方式
        if (zipType != null && zipType.equals("single")) {//multi和single两种方式，默认是multi
            //创建一个临时文件夹
            String dirName = "";
            if (outFileName != null && outFileName.trim().length() > 0) {
                dirName = outFileName;
            } else {
                SimpleDateFormat format = new SimpleDateFormat("yyyy_MM_dd_HH_mm");
                dirName = "报表打包下载_" + format.format(new Date());
            }
            File dir = new File(path + File.separator + dirName);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            //把临时文件夹放到自动清除冗余文件器中
            AutoCleanRedundanceFiles.putRedundanceFiles(path + File.separator + dirName);
            //把文件拷贝到临时文件夹中去
            for (int i = 0; i < filePaths.size(); i++) {
                FileOutputStream outputStream = null;
                FileInputStream inputStream = null;
                try {
                    outputStream = new FileOutputStream(path + File.separator + dirName);
                    inputStream = new FileInputStream(filePaths.get(i));
                    byte[] buffer = new byte[1024];
                    int byteread = 0;
                    int bytesum = 0;
                    while ((byteread = inputStream.read(buffer)) != -1) {
                        bytesum += byteread;
                        outputStream.write(buffer, 0, byteread);
                    }
                    outputStream.flush();
                    outputStream.close();
                    inputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            createZipSingleFile(response, dir, dirName);//下载
            AutoCleanRedundanceFiles.deleteDir(dir);//删除临时文件夹
        } else {//第二种方式
            String dirName = "";
            if (outFileName != null && outFileName.trim().length() > 0) {
                dirName = outFileName;
            } else {
                dirName = "报表打包下载_" + DateTools.getDate("yyyy_MM_dd_HH_mm_ss");
            }
            //找到需要下的文件
            File files[] = new File[filePaths.size()];
            for (int i = 0; i < filePaths.size(); i++) {
                files[i] = new File(filePaths.get(i));
            }
            createZipMultiFile(response, files, dirName);//下载
            //把临时文件夹放到自动清除冗余文件器中
            AutoCleanRedundanceFiles.putRedundanceFiles(path + File.separator + dirName);
        }
    }

    /**
     * 替换文件路径的分隔符（把文件分隔符统一化）
     *
     * @param source
     * @return
     * @author cehnyongqiang
     */
    public static String repalceAllSeparator(String source) {
        if (source != null)
            return source.replaceAll("\\\\", "/").replaceAll("//", "/");
        return source;
    }

    /**
     * 报表类型转换
     * author: chenyongqiang
     *
     * @param fileType
     * @return
     */
    public static String getContentTypeByType(String fileType) {
        if (".txt".equals(fileType)) {
            return "text/plain";
        } else if (".doc".equals(fileType) || ".docx".equals(fileType)) {
            return "application/msword";
        } else if (".xls".equals(fileType) || ".xlsx".equals(fileType)) {
            return "application/vnd.ms-excel";
        } else if (".ppt".equals(fileType)) {
            return "application/vnd.ms-powerpoint";
        } else if (".xml".equals(fileType)) {
            return "text/xml";
        } else if (".pdf".equals(fileType)) {
            return "application/pdf";
        } else if (".jpg".equals(fileType) || ".jpeg".equals(fileType)) {
            return "image/jpeg";
        } else if (".gif".equals(fileType)) {
            return "image/gif";
        } else if (".png".equals(fileType)) {
            return "image/png";
        } else if (".bmp".equals(fileType)) {
            return "image/bmp";
        } else if (".zip".equals(fileType)) {
            return "application/zip";
        } else if (".torrent".equals(fileType)) {
            return "application/x-bittorrent";
        } else if (".mp3".equals(fileType)) {
            return "audio/mp3";
        } else if (".exe".equals(fileType)) {
            return "application/octet-stream";
        } else if (".avi".equals(fileType)) {
            return "video/x-msvideo";
        } else {
            return "application/x-msdownload";
        }
    }

    /**
     * 压缩多个文件（multi方式）
     *
     * @param response
     * @param inputFiles
     * @param zipName
     * @author chenyongqiang
     */
    private static void createZipMultiFile(HttpServletResponse response, File[] inputFiles, String zipName) {
        ZipOutputStream zos = null;
        try {
            response.addHeader("Content-disposition",
                    "attachment;filename=" + new String((zipName + ".rar").getBytes("GBK"), "ISO-8859-1"));
            zos = new ZipOutputStream(response.getOutputStream());
            zos.setEncoding("gb2312");
            for (int i = 0; i < inputFiles.length; i++) {
                writeZipFile(inputFiles[i], zos, "");
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (zos != null)
                    zos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * 压缩文件和文件夹（single方式）
     *
     * @param response
     * @param inputFile
     * @param zipName
     * @author chenyongqiang
     */
    private static void createZipSingleFile(HttpServletResponse response, File inputFile, String zipName) {
        ZipOutputStream zos = null;
        try {
            response.addHeader("Content-disposition",
                    "attachment;filename=" + new String((zipName + ".rar").getBytes("GBK"), "ISO-8859-1"));
            zos = new ZipOutputStream(response.getOutputStream());
            zos.setEncoding("GBK");
            writeZipFile(inputFile, zos, "");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (zos != null)
                    zos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * 采用递归方式
     *
     * @param f
     * @param zos
     * @param hiberarchy
     * @author chenyongqiang
     */
    private static void writeZipFile(File f, ZipOutputStream zos, String hiberarchy) {
        if (f.exists()) {
            if (f.isDirectory()) {
                hiberarchy += f.getName() + File.separator;
                File[] fif = f.listFiles();
                for (int i = 0; i < fif.length; i++) {
                    writeZipFile(fif[i], zos, hiberarchy);
                }
            } else {
                FileInputStream fis = null;
                try {
                    fis = new FileInputStream(f);
                    ZipEntry ze = new ZipEntry(hiberarchy + f.getName());//压缩文件
                    zos.putNextEntry(ze);
                    int nNumber;
                    byte[] buf = new byte[100 * 1024];
                    while ((nNumber = fis.read(buf)) != -1) {
                        zos.write(buf, 0, nNumber);
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (fis != null)
                            fis.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }
        }

    }
}


