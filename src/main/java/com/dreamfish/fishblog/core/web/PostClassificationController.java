package com.dreamfish.fishblog.core.web;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dreamfish.fishblog.core.annotation.RequestAuth;
import com.dreamfish.fishblog.core.annotation.RequestPrivilegeAuth;
import com.dreamfish.fishblog.core.config.ConstConfig;
import com.dreamfish.fishblog.core.entity.PostClass;
import com.dreamfish.fishblog.core.entity.PostDate;
import com.dreamfish.fishblog.core.entity.PostTag;
import com.dreamfish.fishblog.core.entity.User;
import com.dreamfish.fishblog.core.enums.UserPrivileges;
import com.dreamfish.fishblog.core.service.PostClassificationService;
import com.dreamfish.fishblog.core.utils.Result;
import com.dreamfish.fishblog.core.utils.ResultCodeEnum;
import com.dreamfish.fishblog.core.utils.StringUtils;
import com.dreamfish.fishblog.core.utils.response.AuthCode;
import com.dreamfish.fishblog.core.utils.auth.PublicAuth;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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
 * 文章分类与归并接口控制器
 */
@RequestMapping(ConstConfig.API_PUBLIC)
@Validated
@Controller
public class PostClassificationController {

    @Autowired
    private HttpServletRequest request;
    @Autowired
    private HttpServletResponse response;

    @Autowired
    private PostClassificationService postClassificationService;


    //获取所有分类
    @GetMapping(value = "/classes", name = "获取所有分类")
    @ResponseBody
    public Result getClasses(
            @RequestParam(value = "maxCount", required = false, defaultValue = "0")
            @Min(value = 0, message = "最大数必须大于等于0")
                    Integer maxCount,
            @RequestParam(value = "startIndex", required = false, defaultValue = "0")
            @Min(value = 0, message = "开始位置必须大于等于0")
                    Integer startIndex){
        if(startIndex.intValue() == maxCount && startIndex == 0) return Result.success(postClassificationService.getClasses());
        else return Result.success(postClassificationService.getClasses(startIndex, maxCount));
    }
    //删除一些分类
    @DeleteMapping(value = "/classes", name = "删除一些分类")
    @ResponseBody
    public Result deleteClasses(@RequestBody JSONObject jsonObject){

        JSONArray archiveIds = jsonObject.getJSONArray("classes");
        if(archiveIds == null || archiveIds.size() == 0)
            return Result.failure(ResultCodeEnum.BAD_REQUEST);

        List<Integer> ids = new ArrayList<Integer>();
        for(int i =0, size = archiveIds.size(); i<size; i++){
            if(StringUtils.isInteger(archiveIds.getString(i))) ids.add(Integer.parseInt(archiveIds.getString(i)));
            else return Result.failure(ResultCodeEnum.BAD_REQUEST.getCode(), "参数 archives[" + i + "] 类型有误","");
        }

        int authCode = PublicAuth.authCheckIncludeLevelAndPrivileges(request, User.LEVEL_WRITER, UserPrivileges.PRIVILEGE_MANAGE_ALL_CLASSANDTAGS);
        if(authCode < AuthCode.SUCCESS) return Result.failure(ResultCodeEnum.UNAUTHORIZED);
        postClassificationService.deleteClasses(ids);
        return Result.success();
    }

    //获取所有时间组
    @GetMapping(value = "/month", name = "获取所有时间组")
    @ResponseBody
    public Result getDate(
            @RequestParam(value = "maxCount", required = false, defaultValue = "0")
            @Min(value = 0, message = "最大数必须大于等于0")
                    Integer maxCount,
            @RequestParam(value = "startIndex", required = false, defaultValue = "0")
            @Min(value = 0, message = "开始位置必须大于等于0")
                    Integer startIndex){
        if((int)startIndex == maxCount && startIndex == 0) return Result.success(postClassificationService.getDates());
        else return Result.success(postClassificationService.getDates(startIndex, maxCount));
    }


    //获取所有分类(分页)
    @GetMapping(value = "/classes/{pageIndex}/{pageSize}", name = "获取所有分类(分页)")
    @ResponseBody
    public Result getClassesWithPager(
            @PathVariable("pageIndex")
            @Min(value = 0, message = "页数必须大于等于0")
                    Integer pageIndex,
            @PathVariable("pageSize")
            @Min(value = 1, message = "页大小必须大于等于1")
                    Integer pageSize){

        Page<PostClass> result = postClassificationService.getClassesWithPager(pageIndex, pageSize);
        if(result!=null) return Result.success(result);
        else return Result.failure(ResultCodeEnum.INTERNAL_SERVER_ERROR);
    }

    //获取所有标签
    @RequestMapping(value = "/tags", name = "获取所有标签", method = RequestMethod.GET)
    @ResponseBody
    public Result getTags(
            @RequestParam(value = "maxCount", required = false, defaultValue = "0")
            @Min(value = 0, message = "最大数必须大于等于0")
                    Integer maxCount,
            @RequestParam(value = "startIndex", required = false, defaultValue = "0")
            @Min(value = 0, message = "开始位置必须大于等于0")
                    Integer startIndex
    ){
        if((int)startIndex == maxCount && startIndex == 0) return Result.success(postClassificationService.getTags());
        else return Result.success(postClassificationService.getTags(startIndex, maxCount));
    }

    //标签
    //=============================

    //获取标签
    @GetMapping("/tag/{id}")
    @ResponseBody
    public Result getTag(
            @PathVariable("id") Integer id){
        List<PostTag> tags = postClassificationService.findTagsById(id);
        if(tags.size()<1) return Result.failure(ResultCodeEnum.NOT_FOUNT);
        return Result.success(tags.get(0));
    }
    //删除标签
    @DeleteMapping("/tag/{id}")
    @ResponseBody
    @RequestAuth(User.LEVEL_WRITER)
    @RequestPrivilegeAuth(UserPrivileges.PRIVILEGE_MANAGE_ALL_CLASSANDTAGS)
    public Result deleteTag(
            @PathVariable("id") Integer id){
        postClassificationService.deleteTag(id);
        return Result.success();
    }
    //更新标签
    @PutMapping("/tag/{id}")
    @ResponseBody
    @RequestAuth(User.LEVEL_WRITER)
    @RequestPrivilegeAuth(UserPrivileges.PRIVILEGE_MANAGE_ALL_CLASSANDTAGS)
    public Result updateTag(
            @PathVariable("id")
                    Integer id,
            @RequestBody @NonNull
                    PostTag postTag){
        postClassificationService.updateTag(postTag);
        return Result.success();
    }
    //添加标签
    @PostMapping("/tag")
    @ResponseBody
    @RequestAuth(User.LEVEL_WRITER)
    @RequestPrivilegeAuth(UserPrivileges.PRIVILEGE_MANAGE_ALL_CLASSANDTAGS)
    public Result addTag(@RequestBody @NonNull PostTag postTag){
        PostTag newPostTag = postClassificationService.addTag(postTag);
        return Result.success(newPostTag);
    }

    //分类
    //=============================

    //获取分类
    @GetMapping("/class/{idOrUrlName}")
    @ResponseBody
    public Result getClass(
            @PathVariable("idOrUrlName") String idOrUrlName){
        PostClass classes;
        if(StringUtils.isInteger(idOrUrlName)) {
            try {
                classes = postClassificationService.findClassesById(Integer.parseInt(idOrUrlName));
            } catch (NotFoundException e) {
                return Result.failure("404", e.getMessage());
            }
        }else {
            try {
                classes = postClassificationService.findClassesByUrlName(idOrUrlName);
            } catch (NotFoundException e) {
                return Result.failure("404", e.getMessage());
            }
        }
        return Result.success(classes);
    }
    //更新分类
    @PutMapping("/class/{id}")
    @ResponseBody
    @RequestAuth(User.LEVEL_WRITER)
    @RequestPrivilegeAuth(UserPrivileges.PRIVILEGE_MANAGE_ALL_CLASSANDTAGS)
    public Result updateClass(
            @PathVariable("id")
                    Integer id,
            @RequestBody @NonNull
                    PostClass postClass){
        postClassificationService.updateClass(postClass);
        return Result.success();
    }
    //添加分类
    @PostMapping("/class")
    @ResponseBody
    @RequestAuth(User.LEVEL_WRITER)
    @RequestPrivilegeAuth(UserPrivileges.PRIVILEGE_MANAGE_ALL_CLASSANDTAGS)
    public Result addClass(
            @RequestBody @NonNull
                    PostClass postClass){
        PostClass newPostTag = postClassificationService.addClass(postClass);
        return Result.success(newPostTag);
    }
    //删除分类
    @DeleteMapping("/class/{id}")
    @ResponseBody
    @RequestAuth(User.LEVEL_WRITER)
    @RequestPrivilegeAuth(UserPrivileges.PRIVILEGE_MANAGE_ALL_CLASSANDTAGS)
    public Result deleteClass(
            @PathVariable("id") Integer id){
        postClassificationService.deleteClass(id);
        return Result.success();
    }

    //时间组
    //=============================

    //获取时间组
    @GetMapping("/month/{id}")
    @ResponseBody
    public Result getDate(
            @PathVariable("id") Integer id){
        PostDate date = postClassificationService.findDatesById(id);
        if(date == null) return Result.failure(ResultCodeEnum.NOT_FOUNT);
        return Result.success(date);
    }
    //更新时间组
    @PutMapping("/month/{id}")
    @ResponseBody
    @RequestAuth(User.LEVEL_WRITER)
    @RequestPrivilegeAuth(UserPrivileges.PRIVILEGE_MANAGE_ALL_CLASSANDTAGS)
    public Result updateDate(
            @PathVariable("id")
                    Integer id,
            @RequestBody @NonNull
                    PostDate postDate){
        return Result.failure(ResultCodeEnum.NOT_IMPLEMENTED);
    }
    //添加时间组
    @PostMapping("/month")
    @ResponseBody
    @RequestAuth(User.LEVEL_WRITER)
    @RequestPrivilegeAuth(UserPrivileges.PRIVILEGE_MANAGE_ALL_CLASSANDTAGS)
    public Result addDate(
            @RequestBody @NonNull
                    PostDate postDate){
        return Result.failure(ResultCodeEnum.NOT_IMPLEMENTED);
    }
    //删除时间组
    @DeleteMapping("/month/{id}")
    @ResponseBody
    @RequestAuth(User.LEVEL_WRITER)
    @RequestPrivilegeAuth(UserPrivileges.PRIVILEGE_MANAGE_ALL_CLASSANDTAGS)
    public Result deleteDate(
            @PathVariable("id") Integer id){
        postClassificationService.deleteDate(id);
        return Result.success();
    }
}
