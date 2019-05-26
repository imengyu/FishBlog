package com.dreamfish.fishblog.core.service.impl;

import com.dreamfish.fishblog.core.entity.PostMedia;
import com.dreamfish.fishblog.core.entity.User;
import com.dreamfish.fishblog.core.enums.UserPrivileges;
import com.dreamfish.fishblog.core.mapper.PostMapper;
import com.dreamfish.fishblog.core.mapper.UserMapper;
import com.dreamfish.fishblog.core.repository.PostMediaRepository;
import com.dreamfish.fishblog.core.service.ImageStorageService;
import com.dreamfish.fishblog.core.utils.Result;
import com.dreamfish.fishblog.core.utils.ResultCodeEnum;
import com.dreamfish.fishblog.core.utils.StringUtils;
import com.dreamfish.fishblog.core.utils.auth.PublicAuth;
import com.dreamfish.fishblog.core.utils.file.FileUtils;
import com.dreamfish.fishblog.core.utils.log.ActionLog;
import com.dreamfish.fishblog.core.utils.request.ContextHolderUtils;
import com.dreamfish.fishblog.core.utils.response.AuthCode;
import net.coobird.thumbnailator.Thumbnails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.awt.image.BufferedImage;
import java.io.*;

@Service
public class ImageStorageServiceImpl implements ImageStorageService {

    //获取配置文件中图片的路径
    @Value("${fishblog.imagesPath}")
    private String imagesStoragePath = "";
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    public ImageStorageServiceImpl()
    {
        /*
        if(imagesStoragePath.equals(""))
            logger.warn("Failed to locate imagesPath, ImageStorageService will not work currently !");
        if(!new File(imagesStoragePath).exists())
            logger.warn("ImagesPath " + imagesStoragePath + " not exist, ImageStorageService will not work currently !");
        */
    }

    @Autowired
    private PostMediaRepository postMediaRepository = null;
    @Autowired
    private PostMapper postMapper = null;
    @Autowired
    private UserMapper userMapper = null;

    /**
     * 读取图像并返回缩略图
     * @param hash 图片 md5 值
     * @param type 图片类型
     * @param size 图片调整大小，例如 100y100
     * @return 返回图片
     * @throws IOException 文件不存在读取异常
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
     * @param hash 图片 md5 值
     * @param type 图片类型
     * @return 返回图片二进制数组
     * @throws IOException 文件不存在读取异常
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
     * @param hash 图片 md5 值
     * @param type 文件类型 jpg pr png
     * @return 返回操作结果
     */
    @Override
    public Result deleteImage(String hash, String type) {

        String path = bulidImageResourcePath(hash, type);
        File file = new File(path);
        if(!file.exists()) return Result.failure(ResultCodeEnum.NOT_FOUNT);
        if(file.delete()) {
            ActionLog.logUserAction("删除图片资源 : " + hash, ContextHolderUtils.getRequest());
            return Result.success();
        }
        else return Result.failure(ResultCodeEnum.FORIBBEN);
    }

    /**
     * 删除文章媒体库的图片
     * @param postId 文章 ID
     * @param hash 图片 md5 值
     * @return 返回操作结果
     * @throws IOException 文件不存在保存异常
     */
    @Override
    @CacheEvict(value = "blog-image-cache", key = "'image-center-'+#postId")
    public Result deleteImageForPost(Integer postId, String hash) throws IOException {

        //文章检查
        if(postMapper.isPostIdExists(postId) == null)
            return Result.failure(ResultCodeEnum.NOT_FOUNT,"文章 ID " + postId + " 未找到");

        //验证用户权限
        HttpServletRequest request = ContextHolderUtils.getRequest();
        Integer currentUserId = PublicAuth.authGetUseId(request);
        Integer postAuthorId =  postMapper.getPostAuthorId(postId);
        if(currentUserId.intValue() != postAuthorId && (PublicAuth.authCheckIncludeLevelAndPrivileges(request, User.LEVEL_WRITER, UserPrivileges.PRIVILEGE_MANAGE_MEDIA_CENTER) < AuthCode.SUCCESS))
            return Result.failure(ResultCodeEnum.FORIBBEN,"无权限管理此文章媒体库");

        Integer count = postMediaRepository.countByHash(hash);
        if(count == 0)
            return Result.failure(ResultCodeEnum.NOT_FOUNT,"资源 HASH " + hash + " 未找到");

        ActionLog.logUserAction("删除文章图片资源 : " + postId + " : " + hash, request);

        //删除记录
        postMediaRepository.deleteByPostIdAndHash(postId, hash);

        boolean deleted = false;
        if(count == 1){//引用只有1，删除文件
            File imageFile = new File(bulidImageResourcePath(hash, ""));
            if(imageFile.exists())
                deleted = imageFile.delete();
        }

        return Result.success(deleted);
    }

    /**
     * 更新文章图片
     * @param postId 文章 ID
     * @param hash 资源HASH
     * @param media 参数
     * @return 返回操作结果
     * @throws IOException 文件不存在保存异常
     */
    @Override
    @CacheEvict(value = "blog-image-cache", key = "'image-center-'+#postId")
    public Result updateImageForPost(Integer postId, String hash, PostMedia media) throws IOException {

        //验证用户权限
        Result authResult = authForPostMediaCenter(postId);
        if(authResult != null) return authResult;

        if(!hash.equals(media.getHash()))
            return Result.failure(ResultCodeEnum.BAD_REQUEST.getCode(), "hash 不相等");
        if(postId.intValue() != media.getPostId())
            return Result.failure(ResultCodeEnum.BAD_REQUEST.getCode(), "postId 不相等");

        PostMedia oldMedia = postMediaRepository.findByPostIdAndHash(postId, hash);
        media.setId(oldMedia.getId());
        media.setPostId(postId);
        media = postMediaRepository.saveAndFlush(media);

        ActionLog.logUserAction("更新文章图片资源 : " + postId + " : " + hash, ContextHolderUtils.getRequest());

        return Result.success(media);
    }

    /**
     * 上传图片
     * @param file 文件
     * @return 返回操作结果
     * @throws IOException 文件不存在保存异常
     */
    @Override
    public Result uploadImage(MultipartFile file) throws IOException {

        //检查文件是否为空
        if (file == null || file.isEmpty() || StringUtils.isBlank(file.getOriginalFilename()))
            return Result.failure(ResultCodeEnum.UPLOAD_ERROR,"IMG_EMPTY");

        //检查文件类型
        String contentType = file.getContentType();
        if (contentType == null || (!contentType.contains("image/jpeg") && !contentType.contains("image/jpg")))
            return Result.failure(ResultCodeEnum.UPLOAD_ERROR,"IMG_FORMAT_ERROR");

        String md5 = FileUtils.getMd5ByFile(file);
        String fileType = FileUtils.getFileTypeFormName(file.getOriginalFilename());
        String filePath = bulidImageResourcePath(md5, fileType);

        //保存
        if(!new File(filePath).exists())
            FileUtils.saveToFile(file, filePath);

        ActionLog.logUserAction("上传图片资源 : " + md5, ContextHolderUtils.getRequest());

        return Result.success(md5);
    }

    /**
     * 上传文章资源到媒体库
     * @param file 资源文件
     * @param postId 文章 ID
     * @return 返回操作结果
     * @throws IOException 文件不存在保存异常
     */
    @Override
    @CacheEvict(value = "blog-image-cache", key = "'image-center-'+#postId")
    public Result uploadImageForPost(MultipartFile file, Integer postId) throws IOException {

        //验证用户权限
        Result authResult = authForPostMediaCenter(postId);
        if(authResult != null) return authResult;

        //检查文件是否为空
        if (file == null || file.isEmpty() || StringUtils.isBlank(file.getOriginalFilename()))
            return Result.failure(ResultCodeEnum.UPLOAD_ERROR,"IMG_EMPTY");

        //保存文件
        String md5 = FileUtils.getMd5ByFile(file);
        String fileType = FileUtils.getFileTypeFormName(file.getOriginalFilename());
        String filePath = bulidImageResourcePath(md5, fileType);
        if(!new File(filePath).exists())
            FileUtils.saveToFile(file, filePath);


        //插入记录
        PostMedia newMedia = new PostMedia();
        newMedia.setPostId(postId);
        newMedia.setHash(md5);
        newMedia.setType(fileType);

        if(!postMediaRepository.existsByPostIdAndHash(postId, md5))
            newMedia =  postMediaRepository.saveAndFlush(newMedia);

        ActionLog.logUserAction("上传文章图片资源 : " + postId + " : " + md5, ContextHolderUtils.getRequest());

        return Result.success(newMedia);
    }

    /**
     * 上传用户头像
     * @param file 文件
     * @param userId 用户 ID
     * @return 返回操作结果
     * @throws IOException 文件不存在保存异常
     */
    @Override
    public Result uploadImageForUserHead(MultipartFile file, Integer userId) throws IOException {

        //验证用户权限
        HttpServletRequest request = ContextHolderUtils.getRequest();
        Integer currentUserId = PublicAuth.authGetUseId(request);
        if(currentUserId.intValue() != userId)
            return Result.failure(ResultCodeEnum.FORIBBEN,"无权限修改用户头像");

        //检查文件是否为空
        if (file == null || file.isEmpty() || StringUtils.isBlank(file.getOriginalFilename()))
            return Result.failure(ResultCodeEnum.UPLOAD_ERROR,"IMG_EMPTY");
        //检查文件类型
        String contentType = file.getContentType();
        if (contentType == null || !contentType.contains("image/jpeg") && !contentType.contains("image/jpg"))
            return Result.failure(ResultCodeEnum.UPLOAD_ERROR,"IMG_FORMAT_ERROR");

        //保存文件
        String md5 = FileUtils.getMd5ByFile(file);
        String fileType = FileUtils.getFileTypeFormName(file.getOriginalFilename());
        String filePath = bulidImageResourcePath(md5, fileType);
        if(!new File(filePath).exists())
            FileUtils.saveToFile(file, filePath);


        ActionLog.logUserAction("更新用户头像资源 : " + userId + " : " + md5, ContextHolderUtils.getRequest());


        //设置用户头像字段
        userMapper.updateUserHead(userId, md5);
        return Result.success(md5);
    }

    /**
     * 获取文章的媒体库资源
     * @param postId 文章 ID
     * @param pageIndex 页数
     * @param pageSize 页大小
     * @return 返回操作结果
     */
    @Override
    @Cacheable(value = "blog-image-cache", key = "'image-center-'+#postId")
    public Result getImageForPost(Integer postId, Integer pageIndex, Integer pageSize) {

        //验证用户权限
        Result authResult = authForPostMediaCenter(postId);
        if(authResult != null) return authResult;

        //读取
        return Result.success(postMediaRepository.findByPostId(postId, PageRequest.of(pageIndex, pageSize)));
        //return Result.success(postMediaRepository.findAll(PageRequest.of(pageIndex, pageSize)));
    }


    /**
     * 通过一个图片 URL 上传图片
     * @param file 图片 URL
     * @return 返回操作结果
     */
    @Override
    public Result uploadImageByUrl(String file) {

        //TODO

        return Result.failure(ResultCodeEnum.NOT_IMPLEMENTED);
    }

    private Result authForPostMediaCenter(Integer postId){
        //验证权限
        if(postMapper.isPostIdExists(postId) == null)
            return Result.failure(ResultCodeEnum.NOT_FOUNT,"文章 ID " + postId + " 未找到");

        //验证用户权限
        HttpServletRequest request = ContextHolderUtils.getRequest();
        Integer currentUserId = PublicAuth.authGetUseId(request);
        Integer postAuthorId =  postMapper.getPostAuthorId(postId);
        if(currentUserId.intValue() != postAuthorId && (PublicAuth.authCheckIncludeLevelAndPrivileges(request, User.LEVEL_WRITER, UserPrivileges.PRIVILEGE_MANAGE_MEDIA_CENTER) < AuthCode.SUCCESS))
            return Result.failure(ResultCodeEnum.FORIBBEN,"无权限管理此文章媒体库");

        return null;
    }
    private String bulidImageResourcePath(String hash, String type){
        return imagesStoragePath + "/" + hash + (type.equals("") ? "" : ("." + type));
    }
}
