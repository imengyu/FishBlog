package com.dreamfish.fishblog.core.service.impl;

import com.dreamfish.fishblog.core.entity.PostMedia;
import com.dreamfish.fishblog.core.entity.User;
import com.dreamfish.fishblog.core.enums.UserPrivileges;
import com.dreamfish.fishblog.core.mapper.PostMapper;
import com.dreamfish.fishblog.core.repository.PostMediaRepository;
import com.dreamfish.fishblog.core.service.MediaStorageService;
import com.dreamfish.fishblog.core.utils.Result;
import com.dreamfish.fishblog.core.utils.ResultCodeEnum;
import com.dreamfish.fishblog.core.utils.StringUtils;
import com.dreamfish.fishblog.core.utils.auth.PublicAuth;
import com.dreamfish.fishblog.core.utils.file.FileUtils;
import com.dreamfish.fishblog.core.utils.response.AuthCode;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class MediaStorageServiceImpl implements MediaStorageService {

    //  路径以及保存方法来自配置文件
    @Value("${fishblog.images-save-path}")
    private String imagesSavePath = "";
    @Value("${fishblog.images-save-type}")
    private String imagesSaveType = "";
    @Value("${fishblog.videos-save-path}")
    private String videosSavePath = "";
    @Value("${fishblog.videos-save-type}")
    private String videosSaveType = "";
    @Value("${fishblog.files-save-path}")
    private String filesSavePath = "";
    @Value("${fishblog.files-save-type}")
    private String filesSaveType = "";

    @Value("${spring.servlet.multipart.max-file-size}")
    private String maxFileSize = "";
    private long maxFileSizeByte = 0;
    @Value("${fishblog.files-upload-chunk-size}")
    private String uploadChunkSize = "";
    private long uploadChunkSizeByte = 0;


    @Autowired
    private PostMediaRepository mediaRepository = null;
    @Autowired
    private PostMapper postMapper = null;

    @Override
    public Result uploadMedia(MultipartFile file, PostMedia postMedia, HttpServletRequest request) {
        //权限检查
        Result permissionCheckResult = checkUserPermission(postMedia.getPostId(), request);
        if(permissionCheckResult != null) return permissionCheckResult;
        //类型检查
        Result typeCheckResult = checkUploadFileType(file, postMedia);
        if(typeCheckResult != null) return typeCheckResult;

        //检查文件是否为空
        if (file.isEmpty() || StringUtils.isBlank(file.getOriginalFilename()))
            return Result.failure(ResultCodeEnum.UPLOAD_ERROR.getCode(), "文件为空，请重新上传","FILE_EMPTY");




        return null;
    }

    @Override
    public Result uploadMediaBlob(MultipartFile file, String token, Integer blobIndex, PostMedia postMedia, HttpServletRequest request) {

        //检查文件是否为空
        if (file.isEmpty() || StringUtils.isBlank(file.getOriginalFilename()))
            return Result.failure(ResultCodeEnum.UPLOAD_ERROR,"FILE_EMPTY");




        return null;
    }

    /**
     * 获取文件分片以及最大上传大小
     * @param fileSize 文件大小，单位 Byte
     * @return 返回分片参数
     */
    @Override
    public Result uploadMediaGetSize(long fileSize) {
        long maxUploadSize = getMaxFileUploadSize();
        if(fileSize < maxUploadSize){
            Map<String, Object> resultData = new HashMap<>();
            resultData.put("multipart", "false");
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
            Map<String, Object> resultData = new HashMap<>();
            resultData.put("multipart", "true");
            resultData.put("chunkCount", chunkCount);
            resultData.put("chunkSize", chunkSize);
            resultData.put("chunkLastSize", chunkLastSize);
            resultData.put("serverMaxUploadSize", maxUploadSize);
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

        if(postId == 0) return Result.success(mediaRepository.findByResourceType(resType, PageRequest.of(pageIndex, pageSize)));
        else return Result.success(mediaRepository.findByPostIdAndResourceType(postId, resType, PageRequest.of(pageIndex, pageSize)));
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


    /**
     * 基础检查用户是否有权限
     * @param postId 文章 ID
     * @param request 请求
     * @return 有权限则返回 null, 否则返回错误信息
     */
    private Result checkUserPermission(Integer postId, HttpServletRequest request){
        if(postId == 0){
            int authCode = PublicAuth.authCheckIncludeLevelAndPrivileges(request, User.LEVEL_WRITER, UserPrivileges.PRIVILEGE_MANAGE_MEDIA_CENTER);
            if(authCode != AuthCode.SUCCESS)
                return Result.failure(ResultCodeEnum.FORIBBEN.getCode(), "当前用户无权限操作");
        }else{
            if(postMapper.isPostIdExists(postId) == null)
                return Result.failure(ResultCodeEnum.NOT_FOUNT.getCode(), "未找到指定文章");
            int postAuthorId = postMapper.getPostAuthorId(postId);
            if(postAuthorId == PublicAuth.authGetUseId(request))
                return null;
            else {
                int authCode = PublicAuth.authCheckIncludeLevelAndPrivileges(request, User.LEVEL_WRITER, UserPrivileges.PRIVILEGE_MANAGE_ALL_ARCHIVES);
                if(authCode != AuthCode.SUCCESS)
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


}
