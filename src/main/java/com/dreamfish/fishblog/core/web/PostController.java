package com.dreamfish.fishblog.core.web;

import com.dreamfish.fishblog.core.annotation.RequestAuth;
import com.dreamfish.fishblog.core.config.ConstConfig;
import com.dreamfish.fishblog.core.entity.Post;
import com.dreamfish.fishblog.core.entity.User;
import com.dreamfish.fishblog.core.service.PostService;
import com.dreamfish.fishblog.core.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


/**
 * 文章内容操作控制器
 */
@Controller
@RequestMapping(ConstConfig.API_PUBLIC_POST)
public class PostController {

    @Autowired
    private PostService postService;

    //读取文章内容
    @GetMapping("/{idOrUrlName}")
    @ResponseBody
    public Result getPosts(
            @PathVariable("idOrUrlName") String idOrUrlName,
            @RequestParam(value = "authPrivate", required = false, defaultValue = "false") boolean authPrivate){
        return postService.findPostWithIdOrUrlName(idOrUrlName, authPrivate);

    }
    //保获取文章草稿
    @GetMapping("/{id}/draft")
    @ResponseBody
    public Result getPostToDraft(@PathVariable("id") Integer id){
        return postService.getPostDraft(id);
    }

    //保存文章内容至草稿
    @PutMapping("/{id}/draft")
    @ResponseBody
    public Result savePostToDraft(
            @PathVariable("id")
                    Integer id,
            @RequestBody @NonNull
                    Post postData){

        return postService.savePostDraft(id,postData);
    }
    //更新文章内容
    @PutMapping("/{id}")
    @ResponseBody
    public Result updatePosts(
            @PathVariable("id")
                    Integer id,
            @RequestBody @NonNull
                    Post postData){

        return postService.updatePost(id,postData);
    }

    //添加新文章
    @PostMapping("")
    @ResponseBody
    @RequestAuth(User.LEVEL_WRITER)
    public Result addPosts(@RequestBody @NonNull
                    Post postData){
        return postService.addPost(postData);
    }

    //删除文章
    @DeleteMapping("/{id}")
    @ResponseBody
    public Result deletePosts(
            @PathVariable("id")
                    Integer id){
        return postService.deletePost(id);
    }

    //增加文章查看数
    @GetMapping("/updateViewCount")
    @ResponseBody
    public Result updatePostViewCount(
            @RequestParam("id")
                    Integer id){
        return postService.increasePostViewCount(id);
    }


    //增加文章查看数
    @GetMapping("/updateLikeCount")
    @ResponseBody
    @RequestAuth(User.LEVEL_GUEST)
    public Result updateLikeCount(
            @RequestParam("id")
                    Integer id,
            @RequestParam(value = "like", required = false, defaultValue = "true") Boolean like){
        return postService.increasePostLikeCount(id, like);
    }
}
