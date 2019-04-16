package com.dreamfish.fishblog.core.service;

import com.dreamfish.fishblog.core.utils.Result;
import org.springframework.web.multipart.MultipartFile;

import java.awt.image.BufferedImage;
import java.io.IOException;

public interface ImageStorageService {

    BufferedImage getImageAdResize(String hash, String type, String size) throws IOException;
    byte[] getImage(String hash, String type) throws IOException;
    Result getImageForPost(Integer postId, Integer pageIndex, Integer pageSize);
    Result deleteImage(String hash, String type) throws IOException;
    Result uploadImage(MultipartFile file) throws IOException;
    Result uploadImageForPost(MultipartFile file, Integer postId) throws IOException;
    Result uploadImageByUrl(String file) throws IOException;
}
