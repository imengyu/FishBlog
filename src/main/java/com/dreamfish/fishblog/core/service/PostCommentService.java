package com.dreamfish.fishblog.core.service;

import com.dreamfish.fishblog.core.entity.PostComment;
import com.dreamfish.fishblog.core.exception.NoPrivilegeException;
import com.dreamfish.fishblog.core.utils.Result;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface PostCommentService {

    Result getOneComment(Integer postId, Integer commentId);
    Result getCommentsForPost(Integer postId);
    Result getCommentsForPostWithPager(Integer postId, Integer page, Integer pageSize);

    Result addCommentInPost(Integer postId, PostComment postComment, HttpServletRequest request);
    Result deleteCommentInPost(Integer postId, Integer commentId, HttpServletRequest request);
    Result updateCommentInPost(Integer postId, PostComment postComment, HttpServletRequest request);

    Result deleteCommentsForPost(Integer postId, List<Integer> ids, HttpServletRequest request) throws NoPrivilegeException;
}
