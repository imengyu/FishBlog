package com.dreamfish.fishblog.core.utils.video;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

public class VideoUtils {

    /**
     * 获得视频缩略图，获取成功返回true，获取失败返回false
     *
     * @param ffmpegPath   是ffmpeg.exe存放的路径
     * @param path         是视频文件的存放路径
     * @param outImagePath 输出缩略图的保存路径
     * @return
     */
    public static void getVideoImage(String ffmpegPath, String path, String outImagePath) throws Exception {

        File file = new File(path);
        if (!file.exists()) {    //判断视频文件是否存在
            throw new FileNotFoundException("视频源文件不存在：" + path);
        }
        //设置参数
        List<String> commands = new java.util.ArrayList<String>();
        commands.add(ffmpegPath);//这里设置ffmpeg.exe存放的路径
        commands.add("-i");
        commands.add(path);//这里是设置要截取缩略图的视频的路径
        commands.add("-y");
        commands.add("-ss");
        commands.add("10");//这里设置的是要截取视频开始播放多少秒后的图，可以自己设置时间
        commands.add("-t");
        commands.add("0.001");
        commands.add("-s");
        commands.add("352x240");//这里设置输出图片的大小
        commands.add(outImagePath);//这里设置输出的截图的保存路径

        //截取缩略图并保存
        ProcessBuilder builder = new ProcessBuilder();
        builder.command(commands);
        builder.start();

    }
}
