package com.hongYi.h5container.business.utils;

import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.LineNumberReader;
import java.io.OutputStream;
import java.io.Reader;
import java.nio.channels.FileChannel;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Dsc:
 *
 * @author Administrator
 * Date: 18.2.10.
 */

public class FileUtil {

    private static File cacheDir = !isExternalStorageWritable() ? Utils.getApp().getFilesDir() : Utils.getApp().getExternalCacheDir();

    public static String imageDirPath = getCacheDir() + File.separator + "images"; // 图片缓存目录
    public static String DB_DATA = getCacheDir() + File.separator + "db_data"; // 数据缓存目录

    public static String IMAGE_DIR = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath()
            + File.separator + "relate"; // 图片保存目录

    private static final int EOF = -1;
    private static final int DEFAULT_BUFFER_SIZE = 1024 * 4;

    public static String SDCardRoot = Environment.getExternalStorageDirectory() + File.separator;   //SDCard根目录
    public static String APPDIR = "离线数据";              //一级目录

    public static String getCacheDir() {
        return cacheDir.getAbsolutePath();
    }

    /**
     * 创建缓存文件夹
     */
    public static void initCacheFile(Context context) {
        LogUtils.i("initCacheFile");

        final File imageFileDir = new File(SDCardRoot + APPDIR);
        if (!imageFileDir.exists()) {
            boolean isOk = imageFileDir.mkdirs();
            if (isOk) {
                LogUtils.i(imageDirPath + "\n 文件夹创建成功");
            }
        }
//        final File imagesDir = new File(IMAGE_DIR);
//        if (!imagesDir.exists()) {
//            boolean isOk = imagesDir.mkdirs();
//            if (isOk) {
//                LogUtils.i(IMAGE_DIR + "\n 文件夹创建成功");
//            }
//        }
//        final File fileDbData = new File(DB_DATA);
//        if (!fileDbData.exists()) {
//            boolean isOk = fileDbData.mkdirs();
//            if (isOk) {
//                LogUtils.i(DB_DATA + "\n 文件夹创建成功");
//            }
//        }
    }

    /**
     * 创建文件夹
     *
     * @param dir        文件夹路径
     * @param folderName 文件夹名称
     */
    public static String createFolder(String dir, String folderName) {
        String folderPath = dir + folderName;

        //新建一个File，传入文件夹目录
        File file = new File(folderPath);
        //判断文件夹是否存在，如果不存在就创建，否则不创建
        if (!file.exists()) {
            //通过file的mkdirs()方法创建目录中包含却不存在的文件夹
            boolean mkdirs = file.mkdirs();
            if (mkdirs) LogUtils.i(String.format("目录：%s 创建成功", folderName));
            else LogUtils.i(String.format("目录：%s 创建失败", folderName));
        } else {
            LogUtils.i(String.format("目录：%s 已经存在", folderName));
        }

        return file.getAbsolutePath();
    }

    /**
     * 获取自定义app目录
     *
     * @return
     */
    public static String getAppDir() {
        String sdCardPath = getSDCardPath();
        return sdCardPath != null ? sdCardPath + APPDIR + File.separator : null;
    }

    /**
     * 获取SDCard根目录
     *
     * @return
     */
    public static String getSDCardPath() {
        File sdDirFile = null;
        if (isExternalStorageWritable()) {
            sdDirFile = Environment.getExternalStorageDirectory();//获取根目录
        }
        return sdDirFile != null ? sdDirFile.toString() + File.separator : null;
    }


    /**
     * 判断外部存储是否可用
     */
    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        LogUtils.e("请检查SD卡是否可用");
        return false;
    }

    /**
     * 从sd卡取文件
     *
     * @param filename
     * @return
     */
    public String getFileFromSdcard(String filename) {
        ByteArrayOutputStream outputStream = null;
        FileInputStream fis = null;
        try {
            outputStream = new ByteArrayOutputStream();
            File file = new File(Environment.getExternalStorageDirectory(), filename);
            if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
                fis = new FileInputStream(file);
                int len = 0;
                byte[] data = new byte[1024];
                while ((len = fis.read(data)) != -1) {
                    outputStream.write(data, 0, len);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                outputStream.close();
                fis.close();
            } catch (IOException e) {
            }
        }
        return new String(outputStream.toByteArray());
    }


    /**
     * 使用BufferedOutputStream 写入文件
     * BufferedOutputStream在写入的数据量不算大的情况下，速度比BufferedWriter要快
     *
     * @param filePath 文件夹路径
     * @param fileName 文件名
     * @param content  写入内容
     * @return 成功返回true
     */
    public static boolean bufferedOutputStream(String filePath, String fileName, String content, boolean isDelete) {
        boolean isSuccess = false;
        FileOutputStream fos = null;
        BufferedOutputStream bos = null;
        long begin = System.currentTimeMillis();
        try {
            String realFilePath = createFile(filePath, fileName, isDelete);
            if (!TextUtils.isEmpty(realFilePath)) {
                fos = new FileOutputStream(realFilePath, true);
                bos = new BufferedOutputStream(fos);
                bos.write(content.getBytes("utf-8"));
                isSuccess = true;
            }

        } catch (IOException e) {
            e.printStackTrace();
            isSuccess = false;
        } finally {
            try {
                if (bos != null) {
                    bos.flush();
                    bos.close();
                }
                if (fos != null) fos.close();
                System.out.println("BufferedOutputStream执行耗时: " + (System.currentTimeMillis() - begin));

            } catch (IOException e) {
                e.printStackTrace();
                isSuccess = false;
            }
        }
        return isSuccess;
    }


    private static String createFile(String filePath, String fileName, boolean isDelete) {

        LogUtils.d(filePath + "\r\n " + fileName);

        File mFile = null;
        try {
            //传入路径 + 文件名
            mFile = new File(filePath, fileName);
            //判断文件是否存在，存在就删除
            if (isDelete) {
                if (mFile.exists()) {
                    boolean delete = mFile.delete();
                }
            }

            //创建文件
            mFile.createNewFile();
            LogUtils.i(String.format("文件 %s 创建成功", mFile.getAbsolutePath()));
            return mFile.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
            mFile = null;
//            Logger.i(String.format("文件 %s 创建失败", mFile.getAbsolutePath()));
            return null;
        }
    }

    /**
     * 保存文件到sd
     *
     * @param filename
     * @param content
     * @return
     */
    public static boolean saveContentToSdcard(String filename, String content) {
        boolean flag = false;
        FileOutputStream fos = null;
        try {
            File file = new File(Environment.getExternalStorageDirectory(), filename);
            if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
                fos = new FileOutputStream(file);
                fos.write(content.getBytes());
                flag = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            flag = false;
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
            }
        }
        return flag;
    }

    /**
     * 取得文件大小
     *
     * @param f
     * @return
     * @throws Exception
     */
    @SuppressWarnings("resource")
    public static long getFileSizes(File f) throws Exception {
        long size = 0;
        if (f.exists()) {
            FileInputStream fis = null;
            fis = new FileInputStream(f);
            size = fis.available();
        } else {
            f.createNewFile();
        }
        return size;
    }

    /**
     * 递归取得文件夹大小
     *
     * @param dir
     * @return
     * @throws Exception
     */
    public static long getFileSize(File dir) throws Exception {
        long size = 0;
        File flist[] = dir.listFiles();
        for (int i = 0; i < flist.length; i++) {
            if (flist[i].isDirectory()) {
                size = size + getFileSize(flist[i]);
            } else {
                size = size + flist[i].length();
            }
        }
        return size;
    }

    /**
     * 转换文件大小
     *
     * @param fileS
     * @return
     */
    public static String FormetFileSize(long fileS) {
        DecimalFormat df = new DecimalFormat("#.00");
        String fileSizeString = "";
        if (fileS < 1024) {
            fileSizeString = df.format((double) fileS) + "B";
        } else if (fileS < 1048576) {
            fileSizeString = df.format((double) fileS / 1024) + "K";
        } else if (fileS < 1073741824) {
            fileSizeString = df.format((double) fileS / 1048576) + "M";
        } else {
            fileSizeString = df.format((double) fileS / 1073741824) + "G";
        }
        return fileSizeString;
    }

    /**
     * 递归求取目录文件个数
     *
     * @param f
     * @return
     */
    public static long getlist(File f) {
        long size = 0;
        File flist[] = f.listFiles();
        size = flist.length;
        for (int i = 0; i < flist.length; i++) {
            if (flist[i].isDirectory()) {
                size = size + getlist(flist[i]);
                size--;
            }
        }
        return size;
    }

    /**
     * 在根目录下搜索文件
     *
     * @param keyword
     * @return
     */
    public static String searchFile(String keyword) {
        String result = "";
        File[] files = new File("/").listFiles();
        for (File file : files) {
            if (file.getName().indexOf(keyword) >= 0) {
                result += file.getPath() + "\n";
            }
        }
        if (result.equals("")) {
            result = "找不到文件!!";
        }
        return result;
    }

    /**
     * @param file 搜索sdcard文件
     * @param ext  需要进行文件搜索的目录
     */
    public static List<String> search(File file, String[] ext) {
        List<String> list = new ArrayList<String>();
        if (file != null) {
            if (file.isDirectory()) {
                File[] listFile = file.listFiles();
                if (listFile != null) {
                    for (int i = 0; i < listFile.length; i++) {
                        search(listFile[i], ext);
                    }
                }
            } else {
                String filename = file.getAbsolutePath();
                for (int i = 0; i < ext.length; i++) {
                    if (filename.endsWith(ext[i])) {
                        list.add(filename);
                        break;
                    }
                }
            }
        }
        return list;
    }

    /**
     * 查询文件
     *
     * @param file
     * @param keyword
     * @return
     */
    public static List<File> FindFile(File file, String keyword) {
        List<File> list = new ArrayList<File>();
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null) {
                for (File tempf : files) {
                    if (tempf.isDirectory()) {
                        if (tempf.getName().toLowerCase().lastIndexOf(keyword) > -1) {
                            list.add(tempf);
                        }
                        list.addAll(FindFile(tempf, keyword));
                    } else {
                        if (tempf.getName().toLowerCase().lastIndexOf(keyword) > -1) {
                            list.add(tempf);
                        }
                    }
                }
            }
        }
        return list;
    }

    public static double getFilesSize(File file, int sizeType) {
        long blockSize = 0;
        try {
            if (file.isDirectory()) {
                blockSize = getFileSizes(file);
            } else {
                blockSize = getFileSize(file);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("获取文件大小", "获取失败!");
        }
        return FormetFileSize(blockSize, sizeType);
    }

    public static final int SIZETYPE_B = 1;//获取文件大小单位为B的double值
    public static final int SIZETYPE_KB = 2;//获取文件大小单位为KB的double值
    public static final int SIZETYPE_MB = 3;//获取文件大小单位为MB的double值
    public static final int SIZETYPE_GB = 4;//获取文件大小单位为GB的double值

    /**
     * 转换文件大小,指定转换的类型
     *
     * @param fileS
     * @param sizeType
     * @return
     */
    private static double FormetFileSize(long fileS, int sizeType) {
        DecimalFormat df = new DecimalFormat("#.00");
        double fileSizeLong = 0;
        switch (sizeType) {
            case SIZETYPE_B:
                fileSizeLong = Double.valueOf(df.format((double) fileS));
                break;
            case SIZETYPE_KB:
                fileSizeLong = Double.valueOf(df.format((double) fileS / 1024));
                break;
            case SIZETYPE_MB:
                fileSizeLong = Double.valueOf(df.format((double) fileS / 1048576));
                break;
            case SIZETYPE_GB:
                fileSizeLong = Double.valueOf(df.format((double) fileS / 1073741824));
                break;
            default:
                break;
        }
        return fileSizeLong;
    }

    /**
     * searchFile 查找文件并加入到ArrayList 当中去
     *
     * @param context
     * @param keyword
     * @param filepath
     * @return
     */
    public static List<Map<String, Object>> searchFile(Context context, String keyword, File filepath) {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        Map<String, Object> rowItem = null;
        int index = 0;
        // 判断SD卡是否存在
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File[] files = filepath.listFiles();
            if (files.length > 0) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        if (file.getName().toLowerCase().lastIndexOf(keyword) > -1) {
                            rowItem = new HashMap<String, Object>();
                            rowItem.put("number", index); // 加入序列号
                            rowItem.put("fileName", file.getName());// 加入名称
                            rowItem.put("path", file.getPath()); // 加入路径
                            rowItem.put("size", file.length() + ""); // 加入文件大小
                            list.add(rowItem);
                        }
                        // 如果目录可读就执行（一定要加，不然会挂掉）
                        if (file.canRead()) {
                            list.addAll(searchFile(context, keyword, file)); // 如果是目录，递归查找
                        }
                    } else {
                        // 判断是文件，则进行文件名判断
                        try {
                            if (file.getName().indexOf(keyword) > -1 || file.getName().indexOf(keyword.toUpperCase()) > -1) {
                                rowItem = new HashMap<String, Object>();
                                rowItem.put("number", index); // 加入序列号
                                rowItem.put("fileName", file.getName());// 加入名称
                                rowItem.put("path", file.getPath()); // 加入路径
                                rowItem.put("size", file.length() + ""); // 加入文件大小
                                list.add(rowItem);
                                index++;
                            }
                        } catch (Exception e) {
                            Toast.makeText(context, "查找发生错误!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        }
        return list;
    }

    /**
     * 根据后缀得到文件类型
     *
     * @param fileName
     * @param pointIndex
     * @return
     */
    public static String getFileType(String fileName, int pointIndex) {
        String type = fileName.substring(pointIndex + 1).toLowerCase();
        if ("m4a".equalsIgnoreCase(type) || "xmf".equalsIgnoreCase(type) || "ogg".equalsIgnoreCase(type) || "wav".equalsIgnoreCase(type)
                || "m4a".equalsIgnoreCase(type) || "aiff".equalsIgnoreCase(type) || "midi".equalsIgnoreCase(type)
                || "vqf".equalsIgnoreCase(type) || "aac".equalsIgnoreCase(type) || "flac".equalsIgnoreCase(type)
                || "tak".equalsIgnoreCase(type) || "wv".equalsIgnoreCase(type)) {
            type = "ic_file_audio";
        } else if ("mp3".equalsIgnoreCase(type) || "mid".equalsIgnoreCase(type)) {
            type = "ic_file_mp3";
        } else if ("avi".equalsIgnoreCase(type) || "mp4".equalsIgnoreCase(type) || "dvd".equalsIgnoreCase(type)
                || "mid".equalsIgnoreCase(type) || "mov".equalsIgnoreCase(type) || "mkv".equalsIgnoreCase(type)
                || "mp2v".equalsIgnoreCase(type) || "mpe".equalsIgnoreCase(type) || "mpeg".equalsIgnoreCase(type)
                || "mpg".equalsIgnoreCase(type) || "asx".equalsIgnoreCase(type) || "asf".equalsIgnoreCase(type)
                || "flv".equalsIgnoreCase(type) || "navi".equalsIgnoreCase(type) || "divx".equalsIgnoreCase(type)
                || "rm".equalsIgnoreCase(type) || "rmvb".equalsIgnoreCase(type) || "dat".equalsIgnoreCase(type)
                || "mpa".equalsIgnoreCase(type) || "vob".equalsIgnoreCase(type) || "3gp".equalsIgnoreCase(type)
                || "swf".equalsIgnoreCase(type) || "wmv".equalsIgnoreCase(type)) {
            type = "ic_file_video";
        } else if ("bmp".equalsIgnoreCase(type) || "pcx".equalsIgnoreCase(type) || "tiff".equalsIgnoreCase(type)
                || "gif".equalsIgnoreCase(type) || "jpeg".equalsIgnoreCase(type) || "tga".equalsIgnoreCase(type)
                || "exif".equalsIgnoreCase(type) || "fpx".equalsIgnoreCase(type) || "psd".equalsIgnoreCase(type)
                || "cdr".equalsIgnoreCase(type) || "raw".equalsIgnoreCase(type) || "eps".equalsIgnoreCase(type)
                || "gif".equalsIgnoreCase(type) || "jpg".equalsIgnoreCase(type) || "jpeg".equalsIgnoreCase(type)
                || "png".equalsIgnoreCase(type) || "hdri".equalsIgnoreCase(type) || "ai".equalsIgnoreCase(type)) {
            type = "ic_file_image";
        } else if ("ppt".equalsIgnoreCase(type) || "doc".equalsIgnoreCase(type) || "xls".equalsIgnoreCase(type)
                || "pps".equalsIgnoreCase(type) || "xlsx".equalsIgnoreCase(type) || "xlsm".equalsIgnoreCase(type)
                || "pptx".equalsIgnoreCase(type) || "pptm".equalsIgnoreCase(type) || "ppsx".equalsIgnoreCase(type)
                || "maw".equalsIgnoreCase(type) || "mdb".equalsIgnoreCase(type) || "pot".equalsIgnoreCase(type)
                || "msg".equalsIgnoreCase(type) || "oft".equalsIgnoreCase(type) || "xlw".equalsIgnoreCase(type)
                || "wps".equalsIgnoreCase(type) || "rtf".equalsIgnoreCase(type) || "ppsm".equalsIgnoreCase(type)
                || "potx".equalsIgnoreCase(type) || "potm".equalsIgnoreCase(type) || "ppam".equalsIgnoreCase(type)) {
            type = "ic_file_office";
        } else if ("txt".equalsIgnoreCase(type) || "text".equalsIgnoreCase(type) || "chm".equalsIgnoreCase(type)
                || "hlp".equalsIgnoreCase(type) || "pdf".equalsIgnoreCase(type) || "doc".equalsIgnoreCase(type)
                || "docx".equalsIgnoreCase(type) || "docm".equalsIgnoreCase(type) || "dotx".equalsIgnoreCase(type)) {
            type = "ic_file_text";
        } else if ("ini".equalsIgnoreCase(type) || "sys".equalsIgnoreCase(type) || "dll".equalsIgnoreCase(type)
                || "adt".equalsIgnoreCase(type)) {
            type = "ic_file_system";
        } else if ("rar".equalsIgnoreCase(type) || "zip".equalsIgnoreCase(type) || "arj".equalsIgnoreCase(type)
                || "gz".equalsIgnoreCase(type) || "z".equalsIgnoreCase(type) || "7Z".equalsIgnoreCase(type) || "GZ".equalsIgnoreCase(type)
                || "BZ".equalsIgnoreCase(type) || "ZPAQ".equalsIgnoreCase(type)) {
            type = "ic_file_rar";
        } else if ("html".equalsIgnoreCase(type) || "htm".equalsIgnoreCase(type) || "java".equalsIgnoreCase(type)
                || "php".equalsIgnoreCase(type) || "asp".equalsIgnoreCase(type) || "aspx".equalsIgnoreCase(type)
                || "jsp".equalsIgnoreCase(type) || "shtml".equalsIgnoreCase(type) || "xml".equalsIgnoreCase(type)) {
            type = "ic_file_web";
        } else if ("exe".equalsIgnoreCase(type) || "com".equalsIgnoreCase(type) || "bat".equalsIgnoreCase(type)
                || "iso".equalsIgnoreCase(type) || "msi".equalsIgnoreCase(type)) {
            type = "ic_file_exe";
        } else if ("apk".equalsIgnoreCase(type)) {
            type = "ic_file_apk";
        } else {
            type = "ic_file_normal";
        }
        return type;
    }

    /**
     * 改变文件大小显示的内容
     *
     * @param size
     * @return
     */
    public static String changeFileSize(String size) {
        if (Integer.parseInt(size) > 1024) {
            size = Integer.parseInt(size) / 1024 + "K";
        } else if (Integer.parseInt(size) > (1024 * 1024)) {
            size = Integer.parseInt(size) / (1024 * 1024) + "M";
        } else if (Integer.parseInt(size) > (1024 * 1024 * 1024)) {
            size = Integer.parseInt(size) / (1024 * 1024 * 1024) + "G";
        } else {
            size += "B";
        }
        return size;
    }

    /**
     * 得到所有文件
     *
     * @param dir
     * @return
     */
    public static ArrayList<File> getAllFiles(File dir) {
        ArrayList<File> allFiles = new ArrayList<File>();
        // 递归取得目录下的所有文件及文件夹
        File[] files = dir.listFiles();
        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            allFiles.add(file);
            if (file.isDirectory()) {
                getAllFiles(file);
            }
        }
        LogUtils.i(allFiles.size() + "");
        return allFiles;
    }

    /**
     * 判断文件MimeType 类型
     *
     * @param f
     * @return
     */
    public static String getMIMEType(File f) {
        String type = "";
        String fName = f.getName();
        /* 取得扩展名 */
        String end = fName.substring(fName.lastIndexOf(".") + 1, fName.length()).toLowerCase();
        /* 依扩展名的类型决定MimeType */
        if (end.equalsIgnoreCase("m4a") || end.equalsIgnoreCase("mp3") || end.equalsIgnoreCase("mid") || end.equalsIgnoreCase("xmf")
                || end.equalsIgnoreCase("ogg") || end.equalsIgnoreCase("wav")) {
            type = "audio";
        } else if (end.equalsIgnoreCase("3gp") || end.equalsIgnoreCase("mp4")) {
            type = "video";
        } else if (end.equalsIgnoreCase("jpg") || end.equalsIgnoreCase("gif") || end.equalsIgnoreCase("png")
                || end.equalsIgnoreCase("jpeg") || end.equalsIgnoreCase("bmp")) {
            type = "image";
        } else if (end.equalsIgnoreCase("apk")) {
            /* android.permission.INSTALL_PACKAGES */
            type = "application/vnd.android.package-archive";
        } else if (end.equalsIgnoreCase("txt") || end.equalsIgnoreCase("java")) {
            /* android.permission.INSTALL_PACKAGES */
            type = "text";
        } else {
            type = "*";
        }
        /* 如果无法直接打开，就跳出软件列表给用户选择 */
        if (end.equalsIgnoreCase("apk")) {
        } else {
            type += "/*";
        }
        return type;
    }

    /**
     * 拷贝文件
     *
     * @param fromFile
     * @param toFile
     * @throws IOException
     */
    public static void copyFile(File fromFile, String toFile) throws IOException {
        FileInputStream from = null;
        FileOutputStream to = null;
        try {
            from = new FileInputStream(fromFile);
            to = new FileOutputStream(toFile);
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = from.read(buffer)) != -1)
                to.write(buffer, 0, bytesRead); // write
        } finally {
            if (from != null)
                try {
                    from.close();
                } catch (IOException e) {
                    LogUtils.e("IOException>>" + e);
                }
            if (to != null)
                try {
                    to.close();
                } catch (IOException e) {
                    LogUtils.e("IOException>>", e);
                }
        }
    }

    /**
     * 创建文件
     *
     * @param file
     * @return
     */
    public static File createNewFile(File file) {
        try {
            if (file.exists()) {
                return file;
            }
            File dir = file.getParentFile();
            if (!dir.exists()) {
                dir.mkdirs();
            }
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (IOException e) {
            LogUtils.e("IOException", e);
            return null;
        }
        return file;
    }

    /**
     * 创建文件
     *
     * @param path
     */
    public static File createNewFile(String path) {
        File file = new File(path);
        return createNewFile(file);
    }// end method createText()

    /**
     * 删除文件
     *
     * @param path
     */
    public static void deleteFile(String path) {
        File file = new File(path);
        deleteFile(file);
    }

    /**
     * 删除文件
     *
     * @param file
     */
    public static void deleteFile(File file) {
        if (!file.exists()) {
            return;
        }
        if (file.isFile()) {
            file.delete();
        } else if (file.isDirectory()) {
            File files[] = file.listFiles();
            for (int i = 0; i < files.length; i++) {
                deleteFile(files[i]);
            }
        }
        file.delete();
    }

    /**
     * 向Text文件中写入内容
     *
     * @param path
     * @param content
     * @return
     */
    public static boolean write(String path, String content) {
        return write(path, content, false);
    }

    public static boolean write(String path, String content, boolean append) {
        return write(new File(path), content, append);
    }

    public static boolean write(File file, String content) {
        return write(file, content, false);
    }

    /**
     * 写入文件
     *
     * @param file
     * @param content
     * @param append
     * @return
     */
    public static boolean write(File file, String content, boolean append) {
        if (file == null || content.isEmpty()) {
            return false;
        }
        if (!file.exists()) {
            file = createNewFile(file);
        }
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file, append);
            fos.write(content.getBytes());
        } catch (Exception e) {
            LogUtils.e("IOException:", e);
            return false;
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                LogUtils.e("IOException:", e);
            }
            fos = null;
        }
        return true;
    }

    /**
     * 获得文件名
     *
     * @param path
     * @return
     */
    public static String getFileName(String path) {
        if (path.isEmpty()) {
            return null;
        }
        File f = new File(path);
        String name = f.getName();
        f = null;
        return name;
    }

    private static String getFileName(Context context, Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf(File.separator);
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    /**
     * 读取文件内容，从第startLine行开始，读取lineCount行
     *
     * @param file
     * @param startLine
     * @param lineCount
     * @return 读到文字的list, 如果list.size<lineCount则说明读到文件末尾了
     */
    public static List<String> readFile(File file, int startLine, int lineCount) {
        if (file == null || startLine < 1 || lineCount < 1) {
            return null;
        }
        if (!file.exists()) {
            return null;
        }
        FileReader fileReader = null;
        List<String> list = null;
        try {
            list = new ArrayList<String>();
            fileReader = new FileReader(file);
            LineNumberReader lineReader = new LineNumberReader(fileReader);
            boolean end = false;
            for (int i = 1; i < startLine; i++) {
                if (lineReader.readLine() == null) {
                    end = true;
                    break;
                }
            }
            if (end == false) {
                for (int i = startLine; i < startLine + lineCount; i++) {
                    String line = lineReader.readLine();
                    if (line == null) {
                        break;
                    }
                    list.add(line);
                }
            }
        } catch (Exception e) {
            LogUtils.e("read LogUtils error!", e);
        } finally {
            if (fileReader != null) {
                try {
                    fileReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return list;
    }

    /**
     * 创建文件夹
     *
     * @param dir
     * @return
     */
    public static boolean createDir(File dir) {
        try {
            if (!dir.exists()) {
                dir.mkdirs();
            }
            return true;
        } catch (Exception e) {
            LogUtils.e("create dir error", e);
            return false;
        }
    }

    /**
     * 在SD卡上创建目录
     *
     * @param dirName
     */
    public static File creatSDDir(String dirName) {
        File dir = new File(dirName);
        dir.mkdir();
        return dir;
    }

    /**
     * 判断SD卡上的文件是否存在
     */
    public static boolean isFileExist(String fileName) {
        File file = new File(fileName);
        return file.exists();
    }

    /**
     * 将一个InputStream里面的数据写入到SD卡中
     */
    public static File write2SDFromInput(String path, String fileName, InputStream input) {
        File file = null;
        OutputStream output = null;
        try {
            creatSDDir(path);
            file = createNewFile(path + "/" + fileName);
            output = new FileOutputStream(file);
            byte buffer[] = new byte[1024];
            int len = -1;
            while ((len = input.read(buffer)) != -1) {
                output.write(buffer, 0, len);
            }
            output.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                output.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return file;
    }

    /**
     * 读取文件内容 从文件中一行一行的读取文件
     *
     * @param file
     * @return
     */
    public static String readFile(File file) {
        Reader read = null;
        String content = "";
        String result = "";
        BufferedReader br = null;
        try {
            read = new FileReader(file);
            br = new BufferedReader(read);
            while ((content = br.readLine().toString().trim()) != null) {
                result += content + "\r\n";
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                read.close();
                br.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    /**
     * 将图片保存到本地时进行压缩, 即将图片从Bitmap形式变为File形式时进行压缩,
     * 特点是: File形式的图片确实被压缩了, 但是当你重新读取压缩后的file为 Bitmap是,它占用的内存并没有改变
     *
     * @param bmp
     * @param file
     */
    public static void compressBmpToFile(Bitmap bmp, File file) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int options = 80;// 个人喜欢从80开始,
        bmp.compress(Bitmap.CompressFormat.JPEG, options, baos);
        while (baos.toByteArray().length / 1024 > 100) {
            baos.reset();
            options -= 10;
            bmp.compress(Bitmap.CompressFormat.JPEG, options, baos);
        }
        try {
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(baos.toByteArray());
            fos.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 将图片从本地读到内存时,进行压缩 ,即图片从File形式变为Bitmap形式
     * 特点: 通过设置采样率, 减少图片的像素, 达到对内存中的Bitmap进行压缩
     *
     * @param srcPath
     * @return
     */
    public static Bitmap compressImageFromFile(String srcPath, float pixWidth, float pixHeight) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;// 只读边,不读内容
        Bitmap bitmap = BitmapFactory.decodeFile(srcPath, options);
        options.inJustDecodeBounds = false;
        int w = options.outWidth;
        int h = options.outHeight;
        //float pixWidth = 800f;//
        //float pixHeight = 480f;//
        int scale = 1;
        if (w > h && w > pixWidth) {
            scale = (int) (options.outWidth / pixWidth);
        } else if (w < h && h > pixHeight) {
            scale = (int) (options.outHeight / pixHeight);
        }
        if (scale <= 0)
            scale = 1;
        options.inSampleSize = scale;// 设置采样率
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;// 该模式是默认的,可不设
        options.inPurgeable = true;// 同时设置才会有效
        options.inInputShareable = true;// 。当系统内存不够时候图片自动被回收
        bitmap = BitmapFactory.decodeFile(srcPath, options);
        // return compressBmpFromBmp(bitmap);//原来的方法调用了这个方法企图进行二次压缩
        // 其实是无效的,大家尽管尝试
        return bitmap;
    }

    /**
     * 指定分辨率和清晰度的图片压缩
     */
    public void transImage(String fromFile, String toFile, int width, int height, int quality) {
        try {
            Bitmap bitmap = BitmapFactory.decodeFile(fromFile);
            int bitmapWidth = bitmap.getWidth();
            int bitmapHeight = bitmap.getHeight();
            // 缩放图片的尺寸
            float scaleWidth = (float) width / bitmapWidth;
            float scaleHeight = (float) height / bitmapHeight;
            Matrix matrix = new Matrix();
            matrix.postScale(scaleWidth, scaleHeight);
            // 产生缩放后的Bitmap对象
            Bitmap resizeBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmapWidth, bitmapHeight, matrix, false);
            // save file
            File myCaptureFile = new File(toFile);
            FileOutputStream out = new FileOutputStream(myCaptureFile);
            if (resizeBitmap.compress(Bitmap.CompressFormat.JPEG, quality, out)) {
                out.flush();
                out.close();
            }
            if (!bitmap.isRecycled()) {
                bitmap.recycle();//记得释放资源，否则会内存溢出
            }
            if (!resizeBitmap.isRecycled()) {
                resizeBitmap.recycle();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }


    /**
     * 保存图片到手机指定目录
     *
     * @param context
     * @param imgName
     * @param bytes
     */
    public static void saveBitmap(Context context, String imgName, byte[] bytes) {
        if (!isExternalStorageWritable()) {
            //   String filePath = null;
            FileOutputStream fos = null;
            try {
//                filePath = Environment.getExternalStorageDirectory().getCanonicalPath() + "/MyImg";
                File imgDir = new File(imageDirPath);
                if (!imgDir.exists()) {
                    imgDir.mkdirs();
                }
                imgName = imageDirPath + "/" + imgName;
                fos = new FileOutputStream(imgName);
                fos.write(bytes);
                Toast.makeText(context, "图片已保存到" + imageDirPath, Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (fos != null) {
                        fos.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    /**
     * 复制文件
     *
     * @param srcPath  : 源文件全路径
     * @param destPath : 目标文件全路径
     * @return
     */
    public static long copyFile(String srcPath, String destPath) {
        try {
            int position = destPath.lastIndexOf(File.separator);
            String dir = destPath.substring(0, position);
            String newFileName = destPath.substring(position + 1);
            final File cacheDir = new File(dir);
            if (!cacheDir.exists()) {
                cacheDir.mkdirs();
            }
            return copyFile(new File(srcPath), new File(dir), newFileName);
        } catch (Exception e) {
            return 0;
        }

    }

    /**
     * 复制文件(以超快的速度复制文件)
     *
     * @param srcFile     源文件File
     * @param destDir     目标目录File
     * @param newFileName 新文件名
     * @return 实际复制的字节数，如果文件、目录不存在、文件为null或者发生IO异常，返回-1
     */
    @SuppressWarnings("resource")
    public static long copyFile(final File srcFile, final File destDir, String newFileName) {
        long copySizes = 0;
        if (!srcFile.exists()) {
            if (LogUtils.debug) {
                LogUtils.d("源文件不存在");
            }
            copySizes = -1;
        } else if (!destDir.exists()) {
            if (LogUtils.debug) {
                LogUtils.d("目标目录不存在");
            }
            copySizes = -1;
        } else if (newFileName == null) {
            if (LogUtils.debug) {
                LogUtils.d("文件名为null");
            }
            copySizes = -1;
        } else {
            FileChannel fcin = null;
            FileChannel fcout = null;
            try {
                fcin = new FileInputStream(srcFile).getChannel();
                fcout = new FileOutputStream(new File(destDir, newFileName)).getChannel();
                long size = fcin.size();
                fcin.transferTo(0, fcin.size(), fcout);
                copySizes = size;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (fcin != null) {
                        fcin.close();
                    }
                    if (fcout != null) {
                        fcout.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return copySizes;
    }


    /**
     * 删除文件夹内所有文件
     *
     * @param delpath delpath path of file
     * @return boolean the result
     */
    public static boolean deleteAllFile(String delpath) {
        try {
            // create file
            final File file = new File(delpath);

            if (!file.isDirectory()) {
                file.delete();
            } else if (file.isDirectory()) {

                final String[] filelist = file.list();
                final int size = filelist.length;
                for (int i = 0; i < size; i++) {

                    // create new file
                    final File delfile = new File(delpath + "/" + filelist[i]);
                    if (!delfile.isDirectory()) {
                        delfile.delete();
                    } else if (delfile.isDirectory()) {
                        // digui
                        deleteFile(delpath + "/" + filelist[i]);
                    }
                }
                file.delete();
            }
        } catch (Exception ex) {
            if (LogUtils.debug) {
                LogUtils.e(ex);
            }
            return false;
        }
        return true;
    }


    private static FileUtil instance;
    private static final int SUCCESS = 1;
    private static final int FAILED = 0;
    private Context context;
    private FileOperateCallback callback;
    private volatile boolean isSuccess;
    private String errorStr;

    public static FileUtil getInstance(Context context) {
        if (instance == null)
            instance = new FileUtil(context);
        return instance;
    }

    private FileUtil(Context context) {
        this.context = context;
    }

    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (callback != null) {
                if (msg.what == SUCCESS) {
                    callback.onSuccess();
                }
                if (msg.what == FAILED) {
                    callback.onFailed(msg.obj.toString());
                }
            }
        }
    };

    public FileUtil copyAssetsToSD(final String srcPath, final String sdPath) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                copyAssetsToDst(context, srcPath, sdPath);
                if (isSuccess)
                    handler.obtainMessage(SUCCESS).sendToTarget();
                else
                    handler.obtainMessage(FAILED, errorStr).sendToTarget();
            }
        }).start();
        return this;
    }

    public void setFileOperateCallback(FileOperateCallback callback) {
        this.callback = callback;
    }

    private void copyAssetsToDst(Context context, String srcPath, String dstPath) {
        try {
            String fileNames[] = context.getAssets().list(srcPath);
            if (fileNames.length > 0) {
                File file = new File(Environment.getExternalStorageDirectory(), dstPath);
                if (!file.exists()) file.mkdirs();
                for (String fileName : fileNames) {
                    if (!srcPath.equals("")) { // assets 文件夹下的目录
                        copyAssetsToDst(context, srcPath + File.separator + fileName, dstPath + File.separator + fileName);
                    } else { // assets 文件夹
                        copyAssetsToDst(context, fileName, dstPath + File.separator + fileName);
                    }
                }
            } else {
                File outFile = new File(Environment.getExternalStorageDirectory(), dstPath);
                InputStream is = context.getAssets().open(srcPath);
                FileOutputStream fos = new FileOutputStream(outFile);
                byte[] buffer = new byte[1024];
                int byteCount;
                while ((byteCount = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, byteCount);
                }
                fos.flush();
                is.close();
                fos.close();
            }
            isSuccess = true;
        } catch (Exception e) {
            e.printStackTrace();
            errorStr = e.getMessage();
            isSuccess = false;
        }
    }

    public interface FileOperateCallback {
        void onSuccess();

        void onFailed(String error);
    }

    public static File from(Context context, Uri uri) throws IOException {
        InputStream inputStream = context.getContentResolver().openInputStream(uri);
        String fileName = getFileName(context, uri);
        String[] splitName = splitFileName(fileName);
        File tempFile = File.createTempFile(splitName[0], splitName[1]);
        tempFile = rename(tempFile, fileName);
        tempFile.deleteOnExit();
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(tempFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if (inputStream != null) {
            copy(inputStream, out);
            inputStream.close();
        }

        if (out != null) {
            out.close();
        }
        return tempFile;
    }

    private static String[] splitFileName(String fileName) {
        String name = fileName;
        String extension = "";
        int i = fileName.lastIndexOf(".");
        if (i != -1) {
            name = fileName.substring(0, i);
            extension = fileName.substring(i);
        }

        return new String[]{name, extension};
    }

    private static File rename(File file, String newName) {
        File newFile = new File(file.getParent(), newName);
        if (!newFile.equals(file)) {
            if (newFile.exists() && newFile.delete()) {
                Log.d("FileUtil", "Delete old " + newName + " file");
            }
            if (file.renameTo(newFile)) {
                Log.d("FileUtil", "Rename file to " + newName);
            }
        }
        return newFile;
    }

    private static long copy(InputStream input, OutputStream output) throws IOException {
        long count = 0;
        int n;
        byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
        while (EOF != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
            count += n;
        }
        return count;
    }


    public static String getFilePath(Context context, final Uri uri) {
        if (null == uri)
            return null;
        final String scheme = uri.getScheme();
        String data = null;
        if (scheme == null)
            data = uri.getPath();
        else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
            data = uri.getPath();
        } else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
            Cursor cursor = context.getContentResolver().query(uri, new String[]{MediaStore.Images.ImageColumns.DATA}, null, null, null);
            if (null != cursor) {
                if (cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                    if (index > -1) {
                        data = cursor.getString(index);
                    }

                }
                cursor.close();
            }
            if (data == null) {
                data = getImageAbsolutePath(context, uri);
            }

        }
        return data;
    }

    public static Uri getUri(final String filePath) {
        return Uri.fromFile(new File(filePath));
    }

    /**
     * 根据Uri获取图片绝对路径，解决Android4.4以上版本Uri转换
     *
     * @param context
     * @param imageUri
     * @author yaoxing
     * @date 2014-10-12
     */
    @TargetApi(19)
    public static String getImageAbsolutePath(Context context, Uri imageUri) {
        if (context == null || imageUri == null)
            return null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT && DocumentsContract.isDocumentUri(context, imageUri)) {
            if (isExternalStorageDocument(imageUri)) {
                String docId = DocumentsContract.getDocumentId(imageUri);
                String[] split = docId.split(":");
                String type = split[0];
                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            } else if (isDownloadsDocument(imageUri)) {
                String id = DocumentsContract.getDocumentId(imageUri);
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                return getDataColumn(context, contentUri, null, null);
            } else if (isMediaDocument(imageUri)) {
                String docId = DocumentsContract.getDocumentId(imageUri);
                String[] split = docId.split(":");
                String type = split[0];
                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                String selection = MediaStore.Images.Media._ID + "=?";
                String[] selectionArgs = new String[]{split[1]};
                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        } // MediaStore (and general)
        else if ("content".equalsIgnoreCase(imageUri.getScheme())) {
            // Return the remote address
            if (isGooglePhotosUri(imageUri))
                return imageUri.getLastPathSegment();
            return getDataColumn(context, imageUri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(imageUri.getScheme())) {
            return imageUri.getPath();
        }
        return null;
    }

    public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        String column = MediaStore.Images.Media.DATA;
        String[] projection = {column};
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

}
