package com.dreamfish.fishblog.core.service.impl;

import com.dreamfish.fishblog.core.entity.PostMedia;
import com.dreamfish.fishblog.core.entity.User;
import com.dreamfish.fishblog.core.enums.UserPrivileges;
import com.dreamfish.fishblog.core.exception.BadTokenException;
import com.dreamfish.fishblog.core.mapper.PostMapper;
import com.dreamfish.fishblog.core.repository.PostMediaRepository;
import com.dreamfish.fishblog.core.service.MediaStorageService;
import com.dreamfish.fishblog.core.utils.Result;
import com.dreamfish.fishblog.core.utils.ResultCodeEnum;
import com.dreamfish.fishblog.core.utils.StringUtils;
import com.dreamfish.fishblog.core.utils.auth.PublicAuth;
import com.dreamfish.fishblog.core.utils.auth.TokenAuthUtils;
import com.dreamfish.fishblog.core.utils.file.FileUtils;
import com.dreamfish.fishblog.core.utils.response.AuthCode;

import com.dreamfish.fishblog.core.utils.video.VideoUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class MediaStorageServiceImpl implements MediaStorageService {

    //  路径以及保存方法来自配置文件
    @Value("${fishblog.images-save-path}")
    private String imagesSavePath;
    @Value("${fishblog.images-save-type}")
    private String imagesSaveType;
    @Value("${fishblog.images-server-url}")
    private String imagesServerUrl;

    @Value("${fishblog.videos-save-path}")
    private String videosSavePath;
    @Value("${fishblog.videos-save-type}")
    private String videosSaveType;
    @Value("${fishblog.videos-server-url}")
    private String videosServerUrl;
    @Value("${fishblog.videos-ffmpeg-path}")
    private String videosServerFfmpegPath;

    @Value("${fishblog.files-save-path}")
    private String filesSavePath;
    @Value("${fishblog.files-save-type}")
    private String filesSaveType;
    @Value("${fishblog.files-server-url}")
    private String filesServerUrl;

    @Value("${spring.servlet.multipart.max-file-size}")
    private String maxFileSize;
    private long maxFileSizeByte = 0;
    @Value("${fishblog.files-upload-chunk-size}")
    private String uploadChunkSize ;
    private long uploadChunkSizeByte = 0;

    private static final Logger logger = LoggerFactory.getLogger(MediaStorageService.class);


    @Autowired
    private PostMediaRepository mediaRepository = null;
    @Autowired
    private PostMapper postMapper = null;

    @Override
    public Result uploadMedia(MultipartFile file, PostMedia postMedia, HttpServletRequest request) {
        String name = file.getOriginalFilename();
        if(StringUtils.isBlank(name)) name = file.getName();
        return saveFile(file, false, 0, "", name, postMedia, FileUtils.getMd5ByFile(file), request);
    }

    @Override
    public Result uploadMediaBlob(MultipartFile file, Integer blobIndex, String multiUploadToken, String filename, PostMedia postMedia, HttpServletRequest request) {
        if(StringUtils.isBlank(postMedia.getHash()))
            return Result.failure(ResultCodeEnum.BAD_REQUEST.getCode(), "分片上传必须提供原文件完整MD5值");
        if(StringUtils.isBlank(filename))
            return Result.failure(ResultCodeEnum.BAD_REQUEST.getCode(), "分片上传必须提供原完整文件名称");
        return saveFile(file, true, blobIndex, multiUploadToken, filename, postMedia, postMedia.getHash(), request);
    }

    /**
     * 获取文件分片以及最大上传大小
     * @param fileSize 文件大小，单位 Byte
     * @return 返回分片参数
     */
    @Override
    public Result uploadMediaGetSize(long fileSize, PostMedia postMedia) {
        long maxUploadSize = getMaxFileUploadSize();
        if(fileSize < maxUploadSize){
            Map<String, Object> resultData = new HashMap<>();
            resultData.put("multipart", false);
            resultData.put("serverMaxUploadSize", maxUploadSize);
            return Result.success(resultData);
        }else{
            long chunkSize = getFileUploadChunkSize();
            long chunkCount = (fileSize / chunkSize);
            long chunkLastSize = 0;
            if((chunkCount*chunkSize) < fileSize){
                chunkLastSize = fileSize - (chunkCount*chunkSize);
                chunkCount ++;
            }

            if(StringUtils.isBlank(postMedia.getHash()))
                return Result.failure(ResultCodeEnum.BAD_REQUEST.getCode(), "HASH 不能为空");

            String uploadMultiToken = TokenAuthUtils.genToken(300,postMedia.getHash()+":"+chunkCount+":"+chunkSize+":"+chunkLastSize);

            Map<String, Object> resultData = new HashMap<>();
            resultData.put("multipart", true);
            resultData.put("chunkCount", chunkCount);
            resultData.put("chunkSize", chunkSize);
            resultData.put("chunkLastSize", chunkLastSize);
            resultData.put("serverMaxUploadSize", maxUploadSize);
            resultData.put("uploadMultiToken", uploadMultiToken);

            return Result.success(resultData);
        }
    }

    /**
     * 查询媒体数据
     * @param postId 文章ID, 0 则为全局
     * @param pageIndex 页码
     * @param pageSize 页大小
     * @param resType 资源类型
     * @param request 请求
     * @return 返回分页数据
     */
    @Override
    public Result listMedia(Integer postId, Integer pageIndex, Integer pageSize, String resType, HttpServletRequest request) {

        //权限检查
        Result permissionCheckResult = checkUserPermission(postId, request);
        if(permissionCheckResult != null)
            return permissionCheckResult;

        if(postId == 0) return Result.success(mediaRepository.findByResourceType(resType, PageRequest.of(pageIndex, pageSize, Sort.by("uploadDate"))));
        else return Result.success(mediaRepository.findByPostIdAndResourceType(postId, resType, PageRequest.of(pageIndex, pageSize, Sort.by("uploadDate"))));
    }

    /**
     * 删除媒体条目
     * @param mediaId 媒体ID
     * @param request 请求
     * @return 返回操作结果
     */
    @Override
    public Result deleteMedia(Integer mediaId, HttpServletRequest request) {

        //读取
        Optional<PostMedia> mediaResult = mediaRepository.findById(mediaId);
        if(!mediaResult.isPresent())
            return Result.failure(ResultCodeEnum.NOT_FOUNT);
        PostMedia media = mediaResult.get();

        //权限检查
        Result permissionCheckResult = checkUserPermission(media.getPostId(), request);
        if(permissionCheckResult != null)
            return permissionCheckResult;

        File sourceFile = new File(media.getUploadPath());
        if(sourceFile.exists()) sourceFile.delete();

        File previewImageFile = new File(media.getResourcePreviewImage());
        if(previewImageFile.exists()) previewImageFile.delete();

        mediaRepository.deleteById(media.getId());
        return Result.success();
    }

    /**
     * 获取媒体条目
     * @param mediaId 媒体ID
     * @param request 请求
     * @return 返回媒体条目
     */
    @Override
    public Result getMedia(Integer mediaId, HttpServletRequest request) {

        //读取
        Optional<PostMedia> mediaResult = mediaRepository.findById(mediaId);
        if(!mediaResult.isPresent())
            return Result.failure(ResultCodeEnum.NOT_FOUNT);

        PostMedia media = mediaResult.get();
        return Result.success(media);
    }

    /**
     * 更新媒体信息
     * @param mediaId 媒体ID
     * @param postMedia 媒体实体
     * @param request 请求
     * @return 返回结果
     */
    @Override
    public Result updateMedia(Integer mediaId, PostMedia postMedia, HttpServletRequest request) {

        //读取
        Optional<PostMedia> mediaResult = mediaRepository.findById(mediaId);
        if(!mediaResult.isPresent())
            return Result.failure(ResultCodeEnum.NOT_FOUNT);

        PostMedia oldMedia = mediaResult.get();

        //权限检查
        Result permissionCheckResult = checkUserPermission(oldMedia.getPostId(), request);
        if(permissionCheckResult != null)
            return permissionCheckResult;

        oldMedia.setTitle(postMedia.getTitle());

        mediaRepository.saveAndFlush(oldMedia);
        return Result.success(postMedia);
    }

    //
    // Checks and utils
    //

    /**
     * 基础检查用户是否有权限
     * @param postId 文章 ID
     * @param request 请求
     * @return 有权限则返回 null, 否则返回错误信息
     */
    private Result checkUserPermission(Integer postId, HttpServletRequest request){
        if(postId == 0){
            int authCode = PublicAuth.authCheckIncludeLevelAndPrivileges(request, User.LEVEL_WRITER, UserPrivileges.PRIVILEGE_MANAGE_MEDIA_CENTER);
            if(authCode < AuthCode.SUCCESS)
                return Result.failure(ResultCodeEnum.FORIBBEN.getCode(), "当前用户无权限操作");
        }else{
            if(postMapper.isPostIdExists(postId) == null)
                return Result.failure(ResultCodeEnum.NOT_FOUNT.getCode(), "未找到指定文章");
            int postAuthorId = postMapper.getPostAuthorId(postId);
            if(postAuthorId == PublicAuth.authGetUseId(request))
                return null;
            else {
                int authCode = PublicAuth.authCheckIncludeLevelAndPrivileges(request, User.LEVEL_WRITER, UserPrivileges.PRIVILEGE_MANAGE_ALL_ARCHIVES);
                if(authCode < AuthCode.SUCCESS)
                    return Result.failure(ResultCodeEnum.FORIBBEN.getCode(), "当前用户无权限操作此篇文章");
            }
        }
        return null;
    }
    /**
     * 检查文件类型是否合法
     * @param file 文件
     * @param postMedia 媒体信息
     * @return 合法则返回 null, 否则返回错误信息
     */
    private Result checkUploadFileType(MultipartFile file, PostMedia postMedia){

        //检查文件类型
        String contentType = file.getContentType();
        if("images".equals(postMedia.getResourceType())){
            if (contentType == null || !contentType.startsWith("image/"))
                return Result.failure(ResultCodeEnum.UPLOAD_ERROR,"IMG_FORMAT_ERROR");

        }
        if("images".equals(postMedia.getResourceType())){
            if (contentType == null || !contentType.startsWith("video/"))
                return Result.failure(ResultCodeEnum.UPLOAD_ERROR,"VIDEO_FORMAT_ERROR");

        }
        return null;
    }

    /**
     * 获取最大上传大小（字符串参数转数字）
     * @return 最大上传大小Byte
     */
    private long getMaxFileUploadSize(){
        if(maxFileSizeByte == 0)
            maxFileSizeByte = FileUtils.readableFileSizeToByteCount(maxFileSize);
        return maxFileSizeByte;
    }
    /**
     * 获取最大上传大小（字符串参数转数字）
     * @return 最大上传大小Byte
     */
    private long getFileUploadChunkSize(){
        if(uploadChunkSizeByte == 0)
            uploadChunkSizeByte = FileUtils.readableFileSizeToByteCount(uploadChunkSize);
        return uploadChunkSizeByte;
    }
    /**
     * 获取上传文件的MD5值文件名
     * @param file 上传的文件
     * @return MD5值文件名
     */
    private String getFileMD5Name(MultipartFile file, String fileMd5){
        String fileType = FileUtils.getFileTypeFormName(file.getOriginalFilename());
        String fileName = fileMd5;
        if(!StringUtils.isBlank(fileType))
            fileName += "." + fileType;
        return fileName;
    }

    //
    // 保存文件方法
    //

    /**
     * 保存文件入口
     * @param file 文件
     * @param multipart 是否是分片
     * @param multipartUploadToken 分片上传TOKEN
     * @param postMedia 媒体
     * @return 返回操作结果
     */
    private Result saveFile(MultipartFile file, boolean multipart, int blobIndex, String multipartUploadToken, String fileName, PostMedia postMedia, String fileMd5, HttpServletRequest request) {
        //权限检查
        Result permissionCheckResult = checkUserPermission(postMedia.getPostId(), request);
        if(permissionCheckResult != null) return permissionCheckResult;
        //类型检查
        Result typeCheckResult = checkUploadFileType(file, postMedia);
        if(typeCheckResult != null) return typeCheckResult;

        //根据媒体类型进行不同的存储
        if("image".equals(postMedia.getResourceType()))
            return saveFileImages(file, multipart, blobIndex, multipartUploadToken, fileName, postMedia, fileMd5);
        else if("video".equals(postMedia.getResourceType()))
            return saveFileVideo(file, multipart, blobIndex, multipartUploadToken, fileName, postMedia, fileMd5);
        else if("file".equals(postMedia.getResourceType()))
            return saveFileFiles(file, multipart, blobIndex, multipartUploadToken, fileName, postMedia, fileMd5);
        return Result.failure(ResultCodeEnum.UPLOAD_ERROR.getCode(), "未知媒体类型");
    }
    private Result createDir(String dirPath){
        File saveDirFile = new File(dirPath);
        if(!saveDirFile.exists()&&!saveDirFile.isDirectory())
            if(!saveDirFile.mkdirs())
                return Result.failure(ResultCodeEnum.FORIBBEN.getCode(), "无权限创建文件夹（" + dirPath + "），请确认保存文件路径可访问");

        return null;
    }

    //保存路径选择
    private Result saveFileImages(MultipartFile file, boolean multipart, int blobIndex, String multipartUploadToken, String fileName, PostMedia postMedia, String fileMd5) {

        if(imagesSaveType.equals("local")){
            String relativePath = "/" + fileName.substring(0, 2) + "/" + postMedia.getPostId() + "/";
            String fileDir = imagesSavePath + relativePath;

            postMedia.setHash(fileMd5);
            return saveFileToLocalStorage(file, multipart, blobIndex, multipartUploadToken, postMedia, fileDir, fileName, fileMd5, imagesServerUrl + relativePath + fileName);
        }
        return Result.failure(ResultCodeEnum.NOT_IMPLEMENTED.getCode(), "不支持的保存方式：" + imagesSaveType);
    }
    private Result saveFileVideo(MultipartFile file, boolean multipart, int blobIndex, String multipartUploadToken, String fileName, PostMedia postMedia, String fileMd5) {
        if(videosSaveType.equals("local")) {
            if(StringUtils.isBlank(fileName)) fileName = file.getName();
            String relativePath = "/" + postMedia.getPostId() + "/";
            String fileDir = videosSavePath + relativePath;

            postMedia.setHash(fileMd5);
            return saveFileToLocalStorage(file, multipart, blobIndex, multipartUploadToken, postMedia, fileDir, fileName, fileMd5, videosServerUrl + relativePath + fileName);
        }
        return Result.failure(ResultCodeEnum.NOT_IMPLEMENTED.getCode(), "不支持的保存方式：" + videosSaveType);
    }
    private Result saveFileFiles(MultipartFile file, boolean multipart, int blobIndex, String multipartUploadToken, String fileName, PostMedia postMedia, String fileMd5) {
        if(filesSaveType.equals("local")) {
            String relativePath = "/" + postMedia.getPostId() + "/" + fileMd5 + "/";
            String fileDir = filesSavePath + relativePath;

            postMedia.setHash(fileMd5);
            return saveFileToLocalStorage(file, multipart, blobIndex, multipartUploadToken, postMedia, fileDir, fileName, fileMd5,filesServerUrl + relativePath + fileName);
        }
        return Result.failure(ResultCodeEnum.NOT_IMPLEMENTED.getCode(), "不支持的保存方式：" + filesSaveType);
    }

    private Result saveSingleFileToLocalStorage(MultipartFile file, PostMedia postMedia, String saveDir, String saveFileName, String fileMd5, String fileOutLink){
        String filePath = saveDir + saveFileName;

        //记录已存在
        if (mediaRepository.existsByPostIdAndHash(postMedia.getPostId(), postMedia.getHash())) {
            PostMedia postMediaOld = mediaRepository.findByPostIdAndHash(postMedia.getPostId(), postMedia.getHash());

            postMedia.setId(postMediaOld.getId());
            if(postMediaOld.isUploadFinish())
                return Result.success(postMediaOld);
        }

        //Create dir
        Result createDirResult = createDir(saveDir);
        if(createDirResult != null) return createDirResult;

        File saveFile = new File(filePath);
        try {

            //插入数据库记录
            postMedia.setHash(fileMd5);
            postMedia.setUploadFinish(true);
            postMedia.setUploadDate(new Date());
            postMedia.setUploadBlob(1);
            postMedia.setUploadCurrent(0);
            postMedia.setUploadPath(filePath);
            postMedia.setResourcePath(fileOutLink);
            postMedia.setType(FileUtils.getFileTypeFormName(file.getOriginalFilename()));
            postMedia.setContentType(file.getContentType());

            postMedia = mediaRepository.saveAndFlush(postMedia);

            if(!saveFile.exists()) FileUtils.saveToFile(file, filePath);

            return Result.success(postMedia);

        } catch (IOException e) {
            e.printStackTrace();
            logger.warn("无权限写入文件 " + filePath + " 错误：" + e.getMessage());
            return Result.failure(ResultCodeEnum.UPLOAD_ERROR.getCode(), "无权限写入文件 " + filePath + " 错误：" + e.getMessage());
        }
    }
    private Result saveFileToLocalStorage(MultipartFile file, boolean multipart, int blobIndex, String multipartUploadToken, PostMedia postMedia, String saveDir, String saveFileName, String fileMd5, String fileOutLink) {

        //Single file
        if(!multipart) return saveSingleFileToLocalStorage(file, postMedia, saveDir, saveFileName, fileMd5, fileOutLink);
        //Multi part file
        else{

            //Decode token
            String[] tokenData;
            try {
                tokenData = TokenAuthUtils.decodeTokenAndGetData(multipartUploadToken, TokenAuthUtils.TOKEN_DEFAULT_KEY, ":");
            }catch (BadTokenException e){
                return Result.failure(ResultCodeEnum.BAD_REQUEST.getCode(), "分片上传 TOKEN 有误");
            }

            PostMedia postMediaOld = null;
            String tokenMd5 = tokenData[0];

            int blobCount = Integer.parseInt(tokenData[1]);

            if(!tokenMd5.equals(postMedia.getHash()))
                return Result.failure(ResultCodeEnum.BAD_REQUEST.getCode(), "分片上传 HASH 有误 (" + postMedia.getHash()+  "/" + tokenMd5 + ")");

            //已存在记录
            if (mediaRepository.existsByPostIdAndHash(postMedia.getPostId(), postMedia.getHash())) {
                postMediaOld = mediaRepository.findByPostIdAndHash(postMedia.getPostId(), postMedia.getHash());
                postMedia.setId(postMediaOld.getId());
            }

            String saveDirTemp = saveDir + "temp/";
            String filePathTemp = saveDirTemp + "upload-temp-" + fileMd5;
            String filePathReal = saveDir + saveFileName;

            //already Exists
            if(new File(filePathReal).exists()) {
                if(postMediaOld != null && postMediaOld.isUploadFinish())
                    return Result.success(postMediaOld);
                return Result.failure(ResultCodeEnum.FAILED_RES_ALREADY_EXIST);
            }

            if(blobIndex == 0) {

                //Create dir
                Result createDirResult = createDir(saveDirTemp);
                if(createDirResult != null) return createDirResult;

                File saveFile = new File(filePathTemp);
                if(saveFile.exists() && saveFile.delete()) return Result.failure(ResultCodeEnum.FAILED_RES_ALREADY_EXIST);

                try {
                    FileUtils.saveToFile(file, filePathTemp);
                    //插入数据库记录
                    postMedia.setUploadDate(new Date());
                    postMedia.setUploadBlob(blobCount);
                    postMedia.setUploadCurrent(0);
                    postMedia.setUploadFinish(false);
                    postMedia.setUploadTempPath(filePathTemp);
                    postMedia.setResourcePath(fileOutLink);

                    if (!mediaRepository.existsByPostIdAndHash(postMedia.getPostId(), postMedia.getHash()))
                        postMedia = mediaRepository.saveAndFlush(postMedia);
                    return Result.success(postMedia);

                } catch (IOException e) {
                    e.printStackTrace();
                    logger.warn("无权限写入文件 " + filePathTemp + " 错误：" + e.getMessage());
                    return Result.failure(ResultCodeEnum.UPLOAD_ERROR.getCode(), "无权限写入文件 " + filePathTemp + " 错误：" + e.getMessage());
                }

            }else{

                File saveFile = new File(filePathTemp);
                if(!saveFile.exists()) return Result.failure(ResultCodeEnum.UPLOAD_ERROR.getCode(), "BLOB 丢失");
                if(postMediaOld == null) return Result.failure(ResultCodeEnum.UPLOAD_ERROR.getCode(), "BLOB 顺序错误，无记录");
                if(blobIndex <= postMediaOld.getUploadCurrent())
                    return Result.failure(ResultCodeEnum.UPLOAD_ERROR.getCode(), "BLOB 顺序错误，当前顺序 " + (postMediaOld.getUploadCurrent() + 1));

                try {
                    FileUtils.saveToFileAppend(file, filePathTemp);
                } catch (IOException e) {
                    e.printStackTrace();
                    logger.warn("无权限写入文件 " + filePathTemp + " 错误：" + e.getMessage());
                    return Result.failure(ResultCodeEnum.UPLOAD_ERROR.getCode(), "无权限写入文件 " + filePathTemp + " 错误：" + e.getMessage());

                }

                if(blobIndex == blobCount - 1){

                    //This is the last blob
                    postMediaOld.setUploadCurrent(blobCount);
                    postMediaOld.setResourcePath(fileOutLink);
                    postMediaOld.setUploadPath(filePathReal);

                    //Copy file to target path
                    File targetFile = new File(filePathReal);
                    if(targetFile.exists() && !targetFile.delete()) return Result.failure(ResultCodeEnum.FAILED_RES_ALREADY_EXIST);

                    try {
                        Files.move(Paths.get(filePathTemp), Paths.get(filePathReal));
                    } catch (IOException e) {
                        e.printStackTrace();
                        logger.warn("移动文件失败 " + filePathTemp +  " -> " + filePathReal + " 错误：" + e.getMessage());
                        return Result.failure(ResultCodeEnum.UPLOAD_ERROR.getCode(), "移动文件失败 " + filePathTemp +  " -> " + filePathReal + " 错误：" + e.getMessage());
                    }

                    //生成视频缩略图
                    if("video".equals(postMediaOld.getResourceType())) {

                        String videoThumbnail = filePathReal + ".jpg";
                        String videoThumbnailLink = fileOutLink + ".jpg";
                        try {
                            VideoUtils.getVideoImage(videosServerFfmpegPath, filePathReal, videoThumbnail);
                            postMediaOld.setResourcePreviewImage(videoThumbnailLink);
                        } catch (Exception e) {
                            e.printStackTrace();
                            logger.warn("生成视频缩略图失败 " + filePathReal + " 错误：" + e.getMessage());
                        }
                    }

                    //设置ContentType
                    postMedia.setType(FileUtils.getFileTypeFormName(file.getOriginalFilename()));
                    try {
                        postMediaOld.setContentType(Files.probeContentType(Paths.get(filePathReal)));
                    } catch (IOException e) {
                        logger.warn("获取文件 ContentType 败 " + filePathReal + " 错误：" + e.getMessage());
                    }

                    postMediaOld.setUploadTempPath("");
                    postMediaOld.setUploadFinish(true);

                } else if(blobIndex < blobCount) {

                    //Normal blob
                    postMediaOld.setUploadCurrent(blobIndex);
                } else return Result.failure(ResultCodeEnum.UPLOAD_ERROR.getCode(), "当前 BLOB 超出最大 BLOB : " + blobCount);

                postMedia = mediaRepository.saveAndFlush(postMediaOld);
                return Result.success(postMedia);
            }
        }
    }

}
