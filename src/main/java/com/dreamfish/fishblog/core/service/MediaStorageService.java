package com.dreamfish.fishblog.core.service;

import com.dreamfish.fishblog.core.entity.PostMedia;
import com.dreamfish.fishblog.core.utils.Result;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

public interface MediaStorageService {

    Result uploadMedia(MultipartFile file, PostMedia postMedia, HttpServletRequest request);
    Result uploadMediaBlob(MultipartFile file, String token, Integer blobIndex, PostMedia postMedia, HttpServletRequest request);
    Result uploadMediaGetSize(long fileSize);
    Result listMedia(Integer postId, Integer pageIndex, Integer pageSize, String resType, HttpServletRequest request);
    Result deleteMedia(Integer mediaId, HttpServletRequest request);
    Result getMedia(Integer mediaId, HttpServletRequest request);
    Result updateMedia(Integer mediaId, PostMedia postMedia, HttpServletRequest request);
}
