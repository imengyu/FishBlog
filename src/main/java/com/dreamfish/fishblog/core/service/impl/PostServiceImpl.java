package com.dreamfish.fishblog.core.service.impl;

import com.alibaba.fastjson.JSON;
import com.dreamfish.fishblog.core.entity.*;
import com.dreamfish.fishblog.core.enums.UserPrivileges;
import com.dreamfish.fishblog.core.mapper.PostCommentMapper;
import com.dreamfish.fishblog.core.mapper.PostDatesMapper;
import com.dreamfish.fishblog.core.mapper.PostMapper;
import com.dreamfish.fishblog.core.mapper.UserMapper;
import com.dreamfish.fishblog.core.repository.PostDraftRepository;
import com.dreamfish.fishblog.core.repository.PostRepository;
import com.dreamfish.fishblog.core.service.MessagesService;
import com.dreamfish.fishblog.core.service.PostService;
import com.dreamfish.fishblog.core.service.UserService;
import com.dreamfish.fishblog.core.utils.Result;
import com.dreamfish.fishblog.core.utils.ResultCodeEnum;
import com.dreamfish.fishblog.core.utils.StringUtils;
import com.dreamfish.fishblog.core.utils.log.ActionLog;
import com.dreamfish.fishblog.core.utils.request.ContextHolderUtils;
import com.dreamfish.fishblog.core.utils.request.RequestUtils;
import com.dreamfish.fishblog.core.utils.response.AuthCode;
import com.dreamfish.fishblog.core.utils.auth.PublicAuth;
import com.dreamfish.fishblog.core.utils.response.PostErrorCode;
import com.sun.org.apache.xpath.internal.operations.Bool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 文章 服务
 */
@Service
public class PostServiceImpl implements PostService {

    @Autowired
    private PostRepository postRepository = null;
    @Autowired
    private PostMapper postMapper = null;
    @Autowired
    private PostDatesMapper postDatesMapper = null;
    @Autowired
    private PostCommentMapper postCommentMapper = null;
    @Autowired
    private MessagesService messagesService = null;
    @Autowired
    private UserService userService = null;
    @Autowired
    private PostDraftRepository postDraftRepository = null;

    @Override
    public Result findPostWithIdOrUrlName(String idOrUrlName, boolean authForRead) {

        Integer id ;
        if(StringUtils.isInteger(idOrUrlName)) id = Integer.parseInt(idOrUrlName);
        else id = postMapper.getPostIdByUrlName(RequestUtils.encoderURLString(idOrUrlName));
        return findPostWithId(id, authForRead);
    }

    @Override
    @Cacheable(value = "blog-simple-reader-cache", key = "'post_'+#p0")
    public Result findPostWithId(Integer id, boolean authForRead) {
        HttpServletRequest request = ContextHolderUtils.getRequest();
        Post post = postMapper.findById(id);
        if(post==null) return Result.failure(ResultCodeEnum.NOT_FOUNT);
        //访问非公开文章需要验证权限
        if(post.getStatus().intValue() != Post.POST_STATUS_PUBLISH) {
            if(authForRead) {

                Integer authorId = PublicAuth.authGetUseId(request);
                if(authorId <= User.LEVEL_LOCKED) return Result.failure(ResultCodeEnum.UNAUTHORIZED, String.valueOf(authorId));
                if(post.getAuthorId() != authorId.intValue()){
                    int authCode = PublicAuth.authCheckIncludeLevelAndPrivileges(request, User.LEVEL_WRITER, UserPrivileges.PRIVILEGE_MANAGE_ALL_ARCHIVES);
                    if (authCode < AuthCode.SUCCESS)  return Result.failure(ResultCodeEnum.FORIBBEN);
                }
            }
            else return Result.failure(ResultCodeEnum.NOT_FOUNT);
        }

        //Get prv and next post title
        Integer prvId = post.getPostPrvId(),
                nextId = post.getPostNextId();
        if(prvId!=0) post.setPostPrvTitle(postMapper.getPostTitle(prvId));
        if(nextId!=0) post.setPostNextTitle(postMapper.getPostTitle(nextId));

        //Get tag names
        String postTags = post.getTags();
        if(!StringUtils.isBlank(postTags)){
            List<PostTag> tagNames = new ArrayList<>();
            String[] tagIds = postTags.split("-");
            for (String gid : tagIds) {
                if(!StringUtils.isBlank(gid) && StringUtils.isInteger(gid))
                    tagNames.add(postMapper.getTag(Integer.parseInt(gid)));
            }

            post.setPostTagNames(tagNames);
        }

        return Result.success(post);
    }
    @Override
    public Result getPostDraft(Integer postId) {
        Result authResult = writePostAuth(postId, null);
        if(authResult!=null) return authResult;

        Map<String, Object> resultData = new HashMap<>();
        PostDraft postDraft = postDraftRepository.findByBelongPost(postId);
        if(postDraft != null) {
            resultData.put("hasDraft", true);
            resultData.put("json", JSON.parseObject(postDraft.getObjectJson()));
            resultData.put("lastUpdateTime", postDraft.getUpdateTime());
        }else resultData.put("hasDraft", false);
        return Result.success(resultData);
    }
    @Override
    public Result savePostDraft(Integer id, Post post) {
        Result authResult = writePostAuth(id, post);
        if(authResult!=null) return authResult;

        PostDraft postDraft = postDraftRepository.findByBelongPost(id);
        if(postDraft == null) postDraft = new PostDraft();

        postDraft.setBelongPost(id);
        postDraft.setObjectJson(JSON.toJSONString(post));
        postDraft.setUpdateTime(new Date());

        postDraftRepository.saveAndFlush(postDraft);
        return Result.success(post);
    }
    @Override
    @Caching(
            evict = {
                    @CacheEvict(value = "blog-simple-reader-cache", key = "'post_'+#p0"),
                    @CacheEvict(value = "blog-simple-reader-cache", key = "'post_stat_'+#p0"),
                    @CacheEvict(value = "blog-posts-pages-cache", allEntries = true)
            }
    )
    public Result updatePost(Integer id, Post post) {

        Result authResult = writePostAuth(id, post);
        if(authResult!=null) return authResult;

        ActionLog.logUserAction("更新文章："+id, ContextHolderUtils.getRequest());

        //删除草稿
        postDraftRepository.deleteByBelongPost(post.getId());

        return Result.success(postRepository.saveAndFlush(post));
    }
    @Override
    @Caching(
            evict = {
                    @CacheEvict(value = "blog-simple-reader-cache", key = "'post_'+#p0"),
                    @CacheEvict(value = "blog-simple-reader-cache", key = "'post_stat_'+#p0"),
                    @CacheEvict(value = "blog-posts-pages-cache", allEntries = true)
            }
    )
    public Result addPost(Post post) {
        Date now = new Date();

        post.setPostDate(now);
        post = postRepository.saveAndFlush(post);

        //日志
        ActionLog.logUserAction("创建文章："+post.getId(), ContextHolderUtils.getRequest());

        //添加对应归档项

        String nowDateStr = new SimpleDateFormat("yyyy-MM").format(now);
        PostDate dateItem =  postDatesMapper.getDateByDate(nowDateStr);
        if(dateItem == null){
            dateItem = new PostDate();
            dateItem.setCount(1);
            dateItem.setDate(nowDateStr);
            postDatesMapper.addDate(dateItem);
        }else postDatesMapper.updateIncreaseDateCount(dateItem.getId());

        return Result.success(post);
    }
    @Override
    @Caching(
            evict = {
                    @CacheEvict(value = "blog-simple-reader-cache", key = "'post_'+#p0"),
                    @CacheEvict(value = "blog-simple-reader-cache", key = "'post_stat_'+#p0"),
                    @CacheEvict(value = "blog-posts-pages-cache", allEntries = true)
            }
    )
    public Result deletePost(Integer id) {

        Result authResult = writePostAuth(id, null);
        if(authResult!=null) return authResult;

        if(!postRepository.existsById(id)) return Result.failure(ResultCodeEnum.NOT_FOUNT);

        //日志
        ActionLog.logUserAction("删除文章："+id, ContextHolderUtils.getRequest());

        //删除草稿
        postDraftRepository.deleteByBelongPost(id);

        //删除对应归档项
        Date now = new Date();
        PostDate dateItem =  postDatesMapper.getDateByDate( new SimpleDateFormat("yyyy-MM").format(now));
        if(dateItem != null) postDatesMapper.updateDecreaseDateCount(dateItem.getId());

        //删除对应所有的评论
        postCommentMapper.deleteCommentByPostId(id);

        postMapper.deletePost(id);
        return Result.success();
    }

    @Override
    @CacheEvict(value = "blog-simple-reader-cache", key = "'post_stat_'+#p0")
    public Result increasePostViewCount(Integer id) {
        postMapper.increasePostValue(id, "view_count");
        return Result.success();
    }
    @Override
    @CacheEvict(value = "blog-simple-reader-cache", key = "'post_stat_'+#p0")
    public Result increasePostLikeCount(Integer id, Boolean like) {

        Integer userId = PublicAuth.authGetUseId(ContextHolderUtils.getRequest());
        if(userId < AuthCode.SUCCESS) return Result.failure(ResultCodeEnum.FAILED_AUTH);

        String likeUsers = postMapper.getPostLikeUsersById(id);
        if(!StringUtils.isEmpty(likeUsers)){
            if(like && (likeUsers.contains("-" + userId)))
                return Result.failure(ResultCodeEnum.FAILED_MULTIPLE_ACTION.getCode(), "您已经赞过该文章");
            else if(!like && !likeUsers.contains("-" + userId))
                return Result.failure(ResultCodeEnum.FAILED_MULTIPLE_ACTION.getCode(), "您未赞过该文章");
        }else likeUsers = "";

        if(like) {
            likeUsers += "-" + userId;//增加用户ID
            postMapper.increasePostValue(id, "like_count");
        }
        else {
            likeUsers = likeUsers.replace("-" + userId, "");//删除用户ID
            postMapper.decreasePostValue(id, "like_count");
        }

        //通知作者
        Integer postUserId = postMapper.getPostAuthorId(id);
        if(postUserId > 0 && postUserId.intValue() != userId) messagesService.sendMessage(postUserId, 0, "您有收获了一个赞！", userService.getUserNameAutoById(userId) + " 赞了你的文章 <a href=\"/archives/post/"+id+"/\" target=\"_blank\">" + postMapper.getPostTitle(id) + "</a> ！");

        postMapper.updatePostLikeUsersById(id, likeUsers);
        return Result.success();
    }

    //写入权限验证
    private Result writePostAuth(Integer id, Post post){
        HttpServletRequest request = ContextHolderUtils.getRequest();
        //检测
        if(post!=null && post.getId().intValue() != id) return Result.failure(ResultCodeEnum.BAD_REQUEST, String.valueOf(PostErrorCode.POST_REQ_ID_ERROR));
        if(!postRepository.existsById(id)) return Result.failure(ResultCodeEnum.NOT_FOUNT);
        //用户ID
        Integer authorId = PublicAuth.authGetUseId(request);
        if(authorId <= User.LEVEL_LOCKED) return Result.failure(ResultCodeEnum.UNAUTHORIZED, String.valueOf(authorId));
        Integer postAuthorId = postMapper.getPostAuthorId(id);
        if(postAuthorId.intValue() != authorId.intValue()) {
            int authCode = PublicAuth.authCheckIncludeLevelAndPrivileges(request, User.LEVEL_WRITER, UserPrivileges.PRIVILEGE_MANAGE_ALL_ARCHIVES);
            if(authCode < AuthCode.SUCCESS) return Result.failure(ResultCodeEnum.FORIBBEN);
        }
        return null;
    }
}
