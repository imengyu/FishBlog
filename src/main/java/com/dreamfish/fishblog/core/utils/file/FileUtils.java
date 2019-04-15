package com.dreamfish.fishblog.core.utils.file;

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
        String[] strArray = fileName.split("\\.");
        int suffixIndex = strArray.length - 1;
        return strArray[suffixIndex];
    }

    /**
     * 上传文件保存
     *
     * @param file
     * @param path
     * @return
     * @throws IOException
     */
    public static boolean saveToFile(MultipartFile file, String path) throws IOException {
        return Files.copy(file.getInputStream(), Paths.get(path)) > 0;
    }

    /**
     * 写入文本到文件
     *
     * @param str
     * @param path
     * @return
     * @throws IOException
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
     *
     * @param file
     * @return
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
}

