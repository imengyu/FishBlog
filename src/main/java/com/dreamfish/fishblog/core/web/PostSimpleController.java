package com.dreamfish.fishblog.core.web;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dreamfish.fishblog.core.entity.PostSimple;
import com.dreamfish.fishblog.core.exception.NoPrivilegeException;
import com.dreamfish.fishblog.core.service.PostSimpleService;
import com.dreamfish.fishblog.core.utils.Result;
import com.dreamfish.fishblog.core.utils.ResultCodeEnum;
import com.dreamfish.fishblog.core.utils.StringUtils;
import com.dreamfish.fishblog.core.config.ConstConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;
import java.util.ArrayList;
import java.util.List;


/**
 * 文章简要信息操作控制器
 */
@Validated
@Controller
@RequestMapping(ConstConfig.API_PUBLIC_POSTS)
public class PostSimpleController {

    @Autowired
    private PostSimpleService postSimpleService = null;
    //request And response
    @Autowired
    private HttpServletRequest request = null;
    @Autowired
    private HttpServletResponse response = null;


    //获取所有文章
    @RequestMapping(value = "", name = "获取所有文章简要信息", method = RequestMethod.GET)
    @ResponseBody
    public Result getPosts(
            @RequestParam(value = "maxCount", required = false, defaultValue = "0")
            @Min(value = 0, message = "最大数量必须大于0")
                    Integer maxCount,
            @RequestParam(value = "sortBy", required = false, defaultValue = "none")
                    String sortBy
    ){
        List<PostSimple> result = postSimpleService.getSimpleWithMaxCount(maxCount, sortByStrToVal(sortBy));
        if(result!=null) return Result.success(result);
        else return Result.failure(ResultCodeEnum.INTERNAL_SERVER_ERROR);
    }

    //获取文章（自定义分页）
    @RequestMapping(value = "/page/{pageIndex}/{pageSize}", name = "获取文章(自定义分页)", method = RequestMethod.GET)
    @ResponseBody
    public Result getPostsWithPager(
            @PathVariable("pageIndex")
            @Min(value = 0, message = "页数必须大于等于0")
                    Integer pageIndex,
            @PathVariable("pageSize")
            @Min(value = 1, message = "页大小必须大于等于1")
                    Integer pageSize,
            @RequestParam(value = "sortBy", required = false, defaultValue = "none")
                    String sortBy,
            @RequestParam(value = "onlyTag", required = false, defaultValue = "none")
                    String onlyTag,
            @Pattern(regexp = "[0-9]{0,4}-[0-9]{0,2}", message = "日期月份格式")
            @RequestParam(value = "byDate", required = false, defaultValue = "0-0")
                    String byDate,
            @RequestParam(value = "byClass", required = false, defaultValue = "none")
                    String byClass,
            @RequestParam(value = "byUser", required = false, defaultValue = "0")
                    Integer byUser,
            @RequestParam(value = "byStatus", required = false, defaultValue = "none")
                    String byStatus,
             @RequestParam(value = "noTopMost", required = false, defaultValue = "false")
                    Boolean noTopMost) {

        Page<PostSimple> result;
        try {
            result = postSimpleService.getSimplePostsWithPageable(pageIndex, pageSize, sortByStrToVal(sortBy), onlyTag, byDate, byClass, byUser, byStatus, noTopMost);
        } catch (NoPrivilegeException e) {
            return Result.failure(e.getCode().toString(), e.getMessage(), "");
        }
        if(result!=null) return Result.success(result);
        else return Result.failure(ResultCodeEnum.INTERNAL_SERVER_ERROR);
    }


    //删除文章集合
    @RequestMapping(value = "", name = "删除文章集合", method = RequestMethod.DELETE)
    @ResponseBody
    public Result deletePosts(@RequestBody JSONObject jsonObject) {
        JSONArray archiveIds = jsonObject.getJSONArray("archives");
        if(archiveIds == null || archiveIds.size() == 0)
            return Result.failure(ResultCodeEnum.BAD_REQUEST);

        List<Integer> ids = new ArrayList<>();
        for(int i =0, size = archiveIds.size(); i<size; i++){
            if(archiveIds.getString(i) != null && StringUtils.isInteger(archiveIds.getString(i))) ids.add(Integer.parseInt(archiveIds.getString(i)));
            else return Result.failure(ResultCodeEnum.BAD_REQUEST.getCode(), "参数 archives[" + i + "] 类型有误","");
        }
        try {
            return postSimpleService.deleteSomePosts(ids);
        } catch (NoPrivilegeException e) {
            e.printStackTrace();
            return Result.failure(e.getCode().toString(), e.getMessage(), "");
        }
    }

    //获取一些文章的状态
    @RequestMapping(value = "/stat", name = "获取一些文章的状态", method = RequestMethod.POST)
    @ResponseBody
    public Result getPostsStats(@RequestBody JSONObject jsonObject) {
        JSONArray archiveIds = jsonObject.getJSONArray("archives");
        if(archiveIds == null || archiveIds.size() == 0)
            return Result.failure(ResultCodeEnum.BAD_REQUEST);

        List<Integer> ids = new ArrayList<>();
        for(int i =0, size = archiveIds.size(); i<size; i++){
            if(archiveIds.getString(i) != null && StringUtils.isInteger(archiveIds.getString(i))) ids.add(Integer.parseInt(archiveIds.getString(i)));
            else return Result.failure(ResultCodeEnum.BAD_REQUEST.getCode(), "参数 archives[" + i + "] 类型有误","");
        } return postSimpleService.getPostsStats(ids);
    }
    //获取一个文章的状态
    @RequestMapping(value = "/stat/{postId}", name = "获取一个文章的状态", method = RequestMethod.GET)
    @ResponseBody
    public Result getPostStats(@PathVariable("postId") Integer postId) {
        return Result.success(postSimpleService.getPostsStats(postId));
    }

    private int sortByStrToVal(String sortBy){
        if("".equals(sortBy) || "none".equals(sortBy))
            return PostSimpleService.POST_SORT_NONE;
        else if("date".equals(sortBy))
            return PostSimpleService.POST_SORT_BY_DATE;
        else if("name".equals(sortBy))
            return PostSimpleService.POST_SORT_BY_NAME;
        else if("view".equals(sortBy))
            return PostSimpleService.POST_SORT_BY_VIEW;
        return PostSimpleService.POST_SORT_NONE;
    }
    /**
     * 检查页码参数是否有效
     * @param value
     * @return
     */
    private boolean isValidPageIndexOrSize(String value){
        return StringUtils.isInteger(value) && Integer.parseInt(value) >= 0;
    }
}
