package com.dreamfish.fishblog.core.web;

import com.dreamfish.fishblog.core.annotation.RequestAuth;
import com.dreamfish.fishblog.core.annotation.RequestPrivilegeAuth;
import com.dreamfish.fishblog.core.config.ConstConfig;
import com.dreamfish.fishblog.core.entity.User;
import com.dreamfish.fishblog.core.enums.UserPrivileges;
import com.dreamfish.fishblog.core.service.ImageStorageService;
import com.dreamfish.fishblog.core.utils.Result;
import com.dreamfish.fishblog.core.utils.ResultCodeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.awt.image.BufferedImage;
import java.io.IOException;

@Controller
@RequestMapping(ConstConfig.API_PUBLIC)
public class ImageStorageController {

    @Autowired
    private HttpServletResponse response = null;
    @Autowired
    private ImageStorageService imageStorageService = null;

    //读取 JPG 图片(可选调整大小)(默认无后缀)
    @GetMapping(value = "/images/{hash}")
    public void getDefJpegImageSizeable(
            @PathVariable("hash")
                    String hash,
            @Pattern(regexp = "[0-9]{0,5}y[0-9]{0,5}", message = "图片缩放大小参数错误")
            @RequestParam(value = "param", required = false, defaultValue = "0y0")
                String param) throws IOException {
        getJpegImageSizeable(hash, param);
    }
    //删除图片
    @DeleteMapping("/images/{hash}")
    @ResponseBody
    @RequestAuth(value = User.LEVEL_WRITER)
    public Result deleteImage(
            @PathVariable("hash") String hash,
            @RequestParam(value = "type", required = false, defaultValue = "jpg")
                    String type) throws IOException {
        imageStorageService.deleteImage(hash, type);
        return Result.success();
    }

    //读取 JPG 图片(可选调整大小)
    @GetMapping(value = "/images/{hash}.jpg")
    public void getJpegImageSizeable(
            @PathVariable("hash")
                    String hash,
            @Pattern(regexp = "[0-9]{0,5}y[0-9]{0,5}", message = "图片缩放大小参数错误")
            @RequestParam(value = "param", required = false, defaultValue = "0y0")
                    String param) throws IOException {

        response.setContentType(MediaType.IMAGE_JPEG_VALUE);  //设置显示图片
        response.setHeader("Cache-Control", "max-age=604800"); //设置缓存

        if("0y0".equals(param))
            response.getOutputStream().write(imageStorageService.getImage(hash, "jpg"));
        else {
            BufferedImage result = imageStorageService.getImageAdResize(hash, "jpg", param);
            if (result != null) ImageIO.write(result, "jpg", response.getOutputStream());
            else response.setStatus(404);
        }
    }

    //读取 PNG 图片(可选调整大小)
    @GetMapping(value = "/images/{hash}.png")
    @RequestAuth(value = User.LEVEL_WRITER)
    public void getPngImageSizeable(
            @PathVariable("hash")
                    String hash,
            @RequestParam(value = "param", required = false, defaultValue = "0y0")
                    String param) throws IOException {

        response.setContentType(MediaType.IMAGE_PNG_VALUE);  //设置显示图片
        response.setHeader("Cache-Control", "max-age=604800"); //设置缓存

        if("0y0".equals(param))
            response.getOutputStream().write(imageStorageService.getImage(hash, "png"));
        else {
            BufferedImage result = imageStorageService.getImageAdResize(hash, "png", param);
            if (result != null) ImageIO.write(result, "png", response.getOutputStream());
            else response.setStatus(404);
        }
    }

    //=================================

    //上传图片
    @PostMapping("/images")
    @ResponseBody
    @RequestAuth(User.LEVEL_WRITER)
    @RequestPrivilegeAuth(UserPrivileges.PRIVILEGE_MANAGE_MEDIA_CENTER)
    public Result uploadImage(
            @RequestParam(value = "file", required = false) MultipartFile imageFile,
            @RequestParam(value = "url", required = false) String imageUrl) throws IOException {
        if(imageFile!=null) return imageStorageService.uploadImage(imageFile);
        else if(imageUrl!=null) return imageStorageService.uploadImageByUrl(imageUrl);
        else return Result.failure(ResultCodeEnum.BAD_REQUEST);
    }

    //上传用户头像
    @PostMapping("/user/{userId}/head")
    @ResponseBody
    @RequestAuth(User.LEVEL_WRITER)
    public Result uploadUserHeadImage(
            @RequestParam(value = "file") @NotNull MultipartFile imageFile,
            @PathVariable("userId") Integer userId) throws IOException {
        return imageStorageService.uploadImageForUserHead(imageFile, userId);
    }

    //获取文章存储库的图片
    @GetMapping("/images/post/{postId}/{pageIndex}/{pageSize}")
    @ResponseBody
    @RequestAuth(value = User.LEVEL_WRITER)
    public Result ugetImageForPost(
            @PathVariable("postId") Integer postId,
            @PathVariable("pageIndex") Integer pageIndex,
            @PathVariable("pageSize") Integer pageSize) {
        return imageStorageService.getImageForPost(postId, pageIndex, pageSize);
    }

    //为文章上传图片
    @PostMapping("/images/post/{postId}")
    @ResponseBody
    @RequestAuth(value = User.LEVEL_WRITER)
    public Result uploadImageForPost(
            @RequestParam(value = "file", required = false) MultipartFile imageFile,
            @PathVariable("postId") Integer postId) throws IOException {
        return imageStorageService.uploadImageForPost(imageFile, postId);
    }

    //为文章删除图片
    @DeleteMapping("/images/post/{postId}/{hash}")
    @ResponseBody
    @RequestAuth(value = User.LEVEL_WRITER)
    public Result deleteImageForPost(
            @PathVariable("postId") Integer postId,
            @PathVariable("hash") String hash) throws IOException {
        return imageStorageService.deleteImageForPost(postId, hash);
    }
}
