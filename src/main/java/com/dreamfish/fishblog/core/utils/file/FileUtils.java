package com.dreamfish.fishblog.core.utils.file;

import com.dreamfish.fishblog.core.utils.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.math.BigInteger;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;

public class FileUtils {
    /**
     * 从文件名获取文件后缀名
     *
     * @param fileName 文件名
     * @return
     */
    public static String getFileTypeFormName(String fileName) {
        if(StringUtils.isBlank(fileName))
            return "";
        String[] strArray = fileName.split("\\.");
        int suffixIndex = strArray.length - 1;
        return strArray[suffixIndex];
    }

    /**
     * 上传文件保存
     *
     * @param file 上传的文件
     * @param path 目标路径
     * @return 返回是否成功
     * @throws IOException IOException
     */
    public static boolean saveToFile(MultipartFile file, String path) throws IOException {
        return Files.copy(file.getInputStream(), Paths.get(path)) > 0;
    }
    /**
     * 上传文件追加
     *
     * @param file 上传的文件
     * @param path 目标路径
     * @return 返回是否成功
     * @throws IOException IOException
     */
    public static boolean saveToFileAppend(MultipartFile file, String path) throws IOException {

        File orginalFile = new File(path);
        if(orginalFile.exists()) {
            InputStream fis = file.getInputStream();
            FileOutputStream fos = new FileOutputStream(orginalFile, true);
            byte[] buffer = new byte[1024 * 4];
            int n = 0;
            while ((n = fis.read(buffer)) != -1)
                fos.write(buffer, 0, n);
            return true;
        }
        return false;
    }

    /**
     * 写入文本到文件
     *
     * @param str 文本
     * @param path 目标路径
     * @throws IOException IOException
     */
    public static void saveToFile(String str, String path) throws IOException {

        FileWriter fw = new FileWriter(path, true);
        BufferedWriter bw = new BufferedWriter(fw);
        bw.write(str);
        bw.close();
        fw.close();
    }

    /**
     * 计算文件的md5值并且返回
     * @param file 文件
     * @return 返回文件 MD5
     */
    public static String getMd5ByFile(File file) {
        String value = null;
        FileInputStream in = null;
        try {
            in = new FileInputStream(file);
            MappedByteBuffer byteBuffer = in.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, file.length());
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(byteBuffer);
            BigInteger bi = new BigInteger(1, md5.digest());
            value = String.format("%032x", bi);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != in) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return value;
    }

    /**
     * 计算上传文件的 md5 值并且返回
     * @param file 上传的文件
     * @return 返回文件 MD5
     */
    public static String getMd5ByFile(MultipartFile file) {
        String value = null;
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(file.getBytes());
            BigInteger bi = new BigInteger(1, md5.digest());
            value = String.format("%032x", bi);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return value;
    }


    /**
     * 读取文件为 BYTE
     *
     * @param file 数组
     * @return
     */
    public static byte[] readAll(File file) throws IOException {
        InputStream in = new FileInputStream(file.getPath());
        byte[] data = toByteArray(in);
        in.close();
        return data;
    }

    private static byte[] toByteArray(InputStream in) throws IOException {

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024 * 4];
        int n = 0;
        while ((n = in.read(buffer)) != -1) {
            out.write(buffer, 0, n);
        }
        return out.toByteArray();
    }

    public static String getReadableFileSize(BigInteger size) {
        BigInteger v1024 = BigInteger.valueOf(1024);
        BigInteger v100 = BigInteger.valueOf(100);
        //如果字节数少于1024，则直接以B为单位，否则先除于1024，后3位因太少无意义
        if (size.compareTo(v1024) < 0) {
            return size.toString() + "B";
        } else {
            size = size.divide(v1024);
        }
        //如果原字节数除于1024之后，少于1024，则可以直接以KB作为单位
        //因为还没有到达要使用另一个单位的时候
        //接下去以此类推
        if (size.compareTo(v1024) < 0)  return size.toString() + "KB";
        else size = size.divide(v1024);
        if (size.compareTo(v1024) < 0) {
            //因为如果以MB为单位的话，要保留最后1位小数，
            //因此，把此数乘以100之后再取余
            size = size.multiply(v100);
            return size.divide(v100).toString() + "."
                    + size.mod(v100).toString() + "MB";
        } else {
            //否则如果要以GB为单位的，先除于1024再作同样的处理
            size = size.multiply(v100).divide(v1024);
            return size.divide(v100).toString() + "."
                    + size.mod(v100).toString() + "GB";
        }
    }
    /**
     * 把文件大小转为可读大小例如 KB、MB、GB
     * @param size 文件大小，B
     * @return 返回可读大小
     */
    public static String getReadableFileSize(long size) {
        //如果字节数少于1024，则直接以B为单位，否则先除于1024，后3位因太少无意义
        if (size < 1024) {
            return String.valueOf(size) + "B";
        } else {
            size = size / 1024;
        }
        //如果原字节数除于1024之后，少于1024，则可以直接以KB作为单位
        //因为还没有到达要使用另一个单位的时候
        //接下去以此类推
        if (size < 1024) {
            return String.valueOf(size) + "KB";
        } else {
            size = size / 1024;
        }
        if (size < 1024) {
            //因为如果以MB为单位的话，要保留最后1位小数，
            //因此，把此数乘以100之后再取余
            size = size * 100;
            return String.valueOf((size / 100)) + "."
                    + String.valueOf((size % 100)) + "MB";
        } else {
            //否则如果要以GB为单位的，先除于1024再作同样的处理
            size = size * 100 / 1024;
            return String.valueOf((size / 100)) + "."
                    + String.valueOf((size % 100)) + "GB";
        }
    }

    /**
     * 把文件可读大小转为真实Byte数
     * @param readableSize 可读大小例如 KB、MB、GB
     * @return Byte数
     */
    public static long readableFileSizeToByteCount(String readableSize){
        String uint = "B";
        String sNumber = null, sEnd = null;
        if(readableSize.length()<=2){
            sEnd = readableSize.substring(readableSize.length() - 1);
            sNumber = readableSize.substring(0, readableSize.length() - 1);
        }else {
            sEnd = readableSize.substring(readableSize.length() - 2);
            sNumber = readableSize.substring(0, readableSize.length() - 2);

        }
        if(Character.isDigit(sEnd.charAt(0))){
            sEnd = sEnd.substring(1);
            sNumber = readableSize.substring(0, readableSize.length() - 1);
            if(!Character.isDigit(sEnd.charAt(0))){
                uint = sEnd;
                sNumber = readableSize.substring(0, readableSize.length() - 1);
            }
        }else uint = sEnd;

        long size = 0;
        size = Long.parseLong(sNumber);

        if("KB".equals(uint) || "K".equals(uint))
            size = size * 1024;
        else if("MB".equals(uint) || "M".equals(uint))
            size = size * 1048576;
        else if("GB".equals(uint) || "G".equals(uint))
            size = size * 1073741824;
        else if("b".equals(uint))
            size = size / 8;
        else if("Kb".equals(uint))
            size = size * 1048576 / 8;
        else if("Mb".equals(uint))
            size = size * 1073741824 / 8;
        else if("Gb".equals(uint))
            size = size * 1048576 / 8;

        return size;
    }
}

