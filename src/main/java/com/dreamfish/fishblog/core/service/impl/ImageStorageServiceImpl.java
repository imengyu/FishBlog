package com.dreamfish.fishblog.core.service.impl;

import com.dreamfish.fishblog.core.service.ImageStorageService;
import com.dreamfish.fishblog.core.utils.Result;
import com.dreamfish.fishblog.core.utils.ResultCodeEnum;
import com.dreamfish.fishblog.core.utils.StringUtils;
import com.dreamfish.fishblog.core.utils.file.FileUtils;
import net.coobird.thumbnailator.Thumbnails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.awt.image.BufferedImage;
import java.io.*;

@Service
public class ImageStorageServiceImpl implements ImageStorageService {

    //获取配置文件中图片的路径
    @Value("${fishblog.imagesPath}")
    private String imagesStoragePath;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * 读取图像并返回缩略图
     * @param hash
     * @param type
     * @param size
     * @return
     * @throws IOException
     */
    @Override
    public BufferedImage getImageAdResize(String hash, String type, String size) throws IOException {
        String path = bulidImageResourcePath(hash, type);
        File file = new File(path);
        if(!file.exists()) throw new FileNotFoundException();
        String[] sizePars = size.split("y");
        int w = Integer.parseInt(sizePars[0]);
        int h = Integer.parseInt(sizePars[1]);
        return Thumbnails.of(file).size(w, h).outputFormat(type).asBufferedImage();
    }

    /**
     * 读取图像
     * @param hash
     * @param type
     * @return
     * @throws IOException
     */
    @Override
    public  byte[] getImage(String hash, String type) throws IOException {
        String path = bulidImageResourcePath(hash, type);
        File file = new File(path);
        if(!file.exists()) throw new FileNotFoundException();
        return FileUtils.readAll(file);
    }

    /**
     * 删除文件
     * @param hash
     * @param type 文件类型 jpg pr png
     * @return
     * @throws IOException
     */
    @Override
    public Result deleteImage(String hash, String type) throws IOException {

        String path = bulidImageResourcePath(hash, type);
        File file = new File(path);
        if(!file.exists()) return Result.failure(ResultCodeEnum.NOT_FOUNT);
        if(file.delete()) return Result.success();
        else return Result.failure(ResultCodeEnum.FORIBBEN);
    }

    /**
     * 上传图片
     * @param file
     * @param request
     * @return
     * @throws IOException
     */
    @Override
    public Result uploadImage(MultipartFile file) throws IOException {

        if (file == null || file.isEmpty() || StringUtils.isBlank(file.getOriginalFilename()))
            return Result.failure(ResultCodeEnum.UPLOAD_ERROR,"IMG_EMPTY");
        String contentType = file.getContentType();
        if (!contentType.contains(""))
            return Result.failure(ResultCodeEnum.UPLOAD_ERROR,"IMG_FORMAT_ERROR");

        String md5 = FileUtils.getMd5ByFile(file);
        String fileType = FileUtils.getFileTypeFormName(file.getOriginalFilename());
        String filePath = bulidImageResourcePath(md5, fileType);

        if(!new File(filePath).exists())
            FileUtils.saveToFile(file, filePath);

        return Result.success(md5);
    }

    @Override
    public Result uploadImageByUrl(String file) throws IOException {

        //TODO

        return Result.success();
    }


    private String bulidImageResourcePath(String hash, String type){
        return imagesStoragePath + "/" + hash + "." + type;
    }
}
