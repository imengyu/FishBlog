package com.dreamfish.fishblog.core.web;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dreamfish.fishblog.core.config.ConstConfig;
import com.dreamfish.fishblog.core.entity.PostComment;
import com.dreamfish.fishblog.core.exception.NoPrivilegeException;
import com.dreamfish.fishblog.core.service.PostCommentService;
import com.dreamfish.fishblog.core.utils.Result;
import com.dreamfish.fishblog.core.utils.ResultCodeEnum;
import com.dreamfish.fishblog.core.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.Min;
import java.util.ArrayList;
import java.util.List;

/**
 * 文章评论接口控制器
 */
@RequestMapping(ConstConfig.API_PUBLIC)
@Validated
@Controller
public class PostCommentController {

    @Autowired
    private HttpServletRequest request;
    @Autowired
    private HttpServletResponse response;
    @Autowired
    private PostCommentService postCommentService;


    //获取文章评论
    @GetMapping("/post/{postId}/comments")
    @ResponseBody
    public Result getCommentsForPost(
            @PathVariable("postId") Integer postId){
        return postCommentService.getCommentsForPost(postId);
    }
    //删除文章评论
    @DeleteMapping("/post/{postId}/comments")
    @ResponseBody
    public Result delCommentsForPost(@PathVariable("postId") Integer postId, @RequestBody JSONObject jsonObject){
        JSONArray archiveIds = jsonObject.getJSONArray("comments");
        if(archiveIds == null || archiveIds.size() == 0)
            return Result.failure(ResultCodeEnum.BAD_REQUEST);

        List<Integer> ids = new ArrayList<Integer>();
        for(int i =0, size = archiveIds.size(); i<size; i++){
            if(StringUtils.isInteger(archiveIds.getString(i))) ids.add(Integer.parseInt(archiveIds.getString(i)));
            else return Result.failure(ResultCodeEnum.BAD_REQUEST.getCode(), "参数 comments[" + i + "] 类型有误","");
        }
        try {
            return postCommentService.deleteCommentsForPost(postId, ids, request);
        } catch (NoPrivilegeException e) {
            e.printStackTrace();
            return Result.failure(e.getCode().toString(), e.getMessage(), "");
        }
    }

    //获取文章评论（默认分页15）
    @GetMapping("/post/{postId}/comments/{pageIndex}")
    @ResponseBody
    public Result getCommentForPostWithDefaultPager(
            @PathVariable("postId") Integer postId,
            @PathVariable("pageIndex")
            @Min(value = 0, message = "页数必须大于等于0") Integer pageIndex){
        return postCommentService.getCommentsForPostWithPager(postId, pageIndex, 15);
    }
    //获取文章评论（分页）
    @GetMapping("/post/{postId}/comments/{pageIndex}/{pageSize}")
    @ResponseBody
    public Result getCommentForPostWithPager(
            @PathVariable("postId") Integer postId,
            @PathVariable("pageIndex")
            @Min(value = 0, message = "页数必须大于等于0") Integer pageIndex,
            @PathVariable("pageSize")
            @Min(value = 1, message = "页大小必须大于等于1") Integer pageSize){
        return postCommentService.getCommentsForPostWithPager(postId, pageIndex, pageSize);
    }
    //新增文章单条评论
    @PostMapping("/post/{postId}/comments")
    @ResponseBody
    public Result addComment(
            @PathVariable("postId") Integer postId,
            @RequestBody @NonNull PostComment postComment){
        return postCommentService.addCommentInPost(postId, postComment, request);
    }


    //获取文章单条评论
    @GetMapping("/post/{postId}/comments/{id}")
    @ResponseBody
    public Result getComment(
            @PathVariable("postId") Integer postId,
            @PathVariable("id") Integer id) {
        return postCommentService.getOneComment(postId, id);
    }
    //删除文章单条评论
    @DeleteMapping("/post/{postId}/comments/{id}")
    @ResponseBody
    public Result deleteCommentForPost(
            @PathVariable("postId") Integer postId,
            @PathVariable("id") Integer id) {
        return postCommentService.deleteCommentInPost(postId, id, request);
    }
    //更新文章单条评论
    @PutMapping("/post/{postId}/comments/{id}")
    @ResponseBody
    public Result updateComment(
            @PathVariable("postId") Integer postId,
            @PathVariable("id") Integer id,
            @RequestBody @NonNull PostComment postComment){
        if(postComment.getId().intValue() != id) return Result.failure(ResultCodeEnum.BAD_REQUEST);
        return postCommentService.updateCommentInPost(postId, postComment, request);
    }

}
