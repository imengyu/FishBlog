package com.dreamfish.fishblog.core.web;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dreamfish.fishblog.core.config.ConstConfig;
import com.dreamfish.fishblog.core.entity.PostMedia;
import com.dreamfish.fishblog.core.service.MediaStorageService;
import com.dreamfish.fishblog.core.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

/**
 * 媒体库
 */
@Controller
@RequestMapping(ConstConfig.API_PUBLIC)
public class MediaCenterController {

    @Autowired
    private MediaStorageService mediaStorageService;

    @Autowired
    private HttpServletRequest request;

    //上传媒体
    @PostMapping("/media")
    @ResponseBody
    public Result uploadMedia(
            @RequestParam(value = "file") @RequestBody MultipartFile file,
            @RequestParam(value = "media") @RequestBody String postMediaJson) {
        PostMedia postMedia = JSONObject.toJavaObject(JSONObject.parseObject(postMediaJson), PostMedia.class);
        return mediaStorageService.uploadMedia(file, postMedia, request);
    }
    //上传媒体（分片函数）
    @PostMapping("/media/blob")
    @ResponseBody
    public Result uploadMediaBlob(
            @RequestParam(value = "file") @RequestBody MultipartFile file,
            @RequestParam(value = "media") @RequestBody String postMediaJson,
            @RequestParam(value = "blob") Integer blobIndex,
            @RequestParam(value = "filename") String filename,
            @RequestParam(value = "token") String token) {
        PostMedia postMedia = JSONObject.toJavaObject(JSONObject.parseObject(postMediaJson), PostMedia.class);
        return mediaStorageService.uploadMediaBlob(file, blobIndex, token, filename, postMedia, request);
    }
    //获取最大上传大小
    @PostMapping("/media/uploadSize")
    @ResponseBody
    public Result uploadMaxSize(@RequestParam(value = "size") Long fileSize, @RequestBody PostMedia postMedia) {
        return mediaStorageService.uploadMediaGetSize(fileSize, postMedia);
    }

    //列举文章媒体库
    @GetMapping("/post/{postId}/media/{pageIndex}/{pageSize}")
    @ResponseBody
    public Result getMediaForPost(
            @PathVariable("postId") Integer postId,
            @PathVariable("pageIndex") Integer pageIndex,
            @PathVariable("pageSize") Integer pageSize,
            @RequestParam(value = "type") String resType) {
        return mediaStorageService.listMedia(postId, pageIndex, pageSize, resType, request);
    }

    //列举全局媒体库
    @GetMapping("/media/{pageIndex}/{pageSize}")
    @ResponseBody
    public Result getMediaGlobal(
            @PathVariable("pageIndex") Integer pageIndex,
            @PathVariable("pageSize") Integer pageSize,
            @RequestParam(value = "type") String resType) {
        return mediaStorageService.listMedia(0, pageIndex, pageSize, resType, request);
    }

    //删除媒体条目
    @ResponseBody
    @DeleteMapping("/media/{id}")
    public Result deleteMedia(
            @PathVariable("id") Integer id) {
        return mediaStorageService.deleteMedia(id, request);
    }
    @ResponseBody
    @GetMapping("/media/{id}")
    public Result getMedia(
            @PathVariable("id") Integer id) {
        return mediaStorageService.getMedia(id, request);
    }

    //更新媒体条目信息
    @ResponseBody
    @PutMapping("/media/{id}")
    public Result updateMedia(
            @PathVariable("id") Integer id,
            @RequestBody PostMedia media) {
        return mediaStorageService.updateMedia(id, media, request);
    }
}
