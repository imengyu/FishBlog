package com.dreamfish.fishblog.core.service;

import com.dreamfish.fishblog.core.entity.Post;
import com.dreamfish.fishblog.core.entity.PostSimple;
import com.dreamfish.fishblog.core.utils.Result;
import org.springframework.data.domain.Page;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface PostService {

    Result findPostWithIdOrUrlName(String idOrUrlName, boolean authForRead);
    Result updatePost(Integer id, Post post);
    Result addPost(Post post);
    Result deletePost(Integer id);

    Result increasePostViewCount(Integer id);
}
