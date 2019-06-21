package com.dreamfish.fishblog.core.service;

import com.dreamfish.fishblog.core.entity.PostMedia;
import com.dreamfish.fishblog.core.utils.Result;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public interface MediaStorageService {

    Result uploadMedia(MultipartFile file, PostMedia postMedia, HttpServletRequest request);
    Result uploadMediaBlob(MultipartFile file, Integer blobIndex, String multiUploadToken, String filename, PostMedia postMedia, HttpServletRequest request);
    Result uploadMediaGetSize(long fileSize, PostMedia postMedia);
    Result listMedia(Integer postId, Integer pageIndex, Integer pageSize, String resType, HttpServletRequest request);
    Result deleteMedia(Integer mediaId, HttpServletRequest request);
    Result getMedia(Integer mediaId, HttpServletRequest request);
    Result updateMedia(Integer mediaId, PostMedia postMedia, HttpServletRequest request);
    Result uploadImageForUserHead(MultipartFile file, Integer userId) throws IOException;
}
