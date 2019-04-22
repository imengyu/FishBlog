package com.dreamfish.fishblog.core.service;

import com.dreamfish.fishblog.core.entity.Post;
import com.dreamfish.fishblog.core.utils.Result;

public interface PostService {

    Result findPostWithIdOrUrlName(String idOrUrlName, boolean authForRead);
    Result findPostWithId(Integer id, boolean authForRead);
    Result updatePost(Integer id, Post post);
    Result addPost(Post post);
    Result deletePost(Integer id);
    Result increasePostViewCount(Integer id);
    Result increasePostLikeCount(Integer id, Boolean like);
}
