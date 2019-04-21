package com.dreamfish.fishblog.core.service.impl;

import com.dreamfish.fishblog.core.entity.Post;
import com.dreamfish.fishblog.core.entity.PostSimple;
import com.dreamfish.fishblog.core.entity.PostStat;
import com.dreamfish.fishblog.core.entity.User;
import com.dreamfish.fishblog.core.enums.UserPrivileges;
import com.dreamfish.fishblog.core.exception.InvalidArgumentException;
import com.dreamfish.fishblog.core.exception.NoPrivilegeException;
import com.dreamfish.fishblog.core.mapper.PostSimpleMapper;
import com.dreamfish.fishblog.core.repository.PostSimpleRepository;
import com.dreamfish.fishblog.core.service.PostSimpleService;
import com.dreamfish.fishblog.core.utils.Result;
import com.dreamfish.fishblog.core.utils.StringUtils;
import com.dreamfish.fishblog.core.utils.auth.PublicAuth;
import com.dreamfish.fishblog.core.utils.log.ActionLog;
import com.dreamfish.fishblog.core.utils.request.ContextHolderUtils;
import com.dreamfish.fishblog.core.utils.response.AuthCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 文章简要信息 服务
 */
@Service
public class PostSimpleServiceImpl implements PostSimpleService {

    @Autowired
    private PostSimpleMapper postSimpleMapper = null;
    @Autowired
    private PostSimpleRepository postSimpleRepository = null;

    @Override
    @Cacheable(value = "blog-posts-pages-cache")
    public List<PostSimple> getSimpleWithMaxCount(Integer maxCount, Integer soryBy) {
        if(soryBy==POST_SORT_NONE) {
            return postSimpleMapper.getAllPostsWithLimit(maxCount);
        }else if(soryBy==POST_SORT_NONE) {
            return postSimpleMapper.getAllPostsWithLimitOrderBy(maxCount, "top_most DESC, title ASC");
        }else if(soryBy==POST_SORT_BY_DATE) {
            return postSimpleMapper.getAllPostsWithLimitOrderBy(maxCount,"top_most DESC, post_date DESC");
        }else if(soryBy==POST_SORT_BY_VIEW) {
            return postSimpleMapper.getAllPostsWithLimitOrderBy(maxCount,"top_most DESC, view_count DESC, post_date DESC");
        }
        return null;
    }
    @Override
    public List<PostSimple> getSimplePosts(Integer page, Integer pageSize, Integer soryBy) {
        return getSimplePosts(page, pageSize, soryBy, "none");
    }
    @Override
    @Cacheable(value = "blog-posts-pages-cache")
    public List<PostSimple> getSimplePosts(Integer page, Integer pageSize, Integer soryBy, String onlyTag) {
        Integer startIndex = page*pageSize;
        if("none".equals(onlyTag)) {
            if (soryBy == POST_SORT_NONE) {
                if (page == -1 && pageSize == -1) return postSimpleMapper.getAllPostsOrderBy("top_most DESC");
                else return postSimpleMapper.getPostsOrderBy(startIndex, pageSize, "top_most DESC");
            } else if (soryBy == POST_SORT_BY_NAME) {
                if (page == -1 && pageSize == -1) return postSimpleMapper.getAllPostsOrderBy("top_most DESC, title ASC");
                else return postSimpleMapper.getPostsOrderBy(startIndex, pageSize, "top_most DESC, title ASC");
            } else if (soryBy == POST_SORT_BY_DATE) {
                if (page == -1 && pageSize == -1) return postSimpleMapper.getAllPostsOrderBy("top_most DESC, post_date DESC");
                else return postSimpleMapper.getPostsOrderBy(startIndex, pageSize, "top_most DESC, post_date DESC");
            } else if (soryBy == POST_SORT_BY_VIEW) {
                if (page == -1 && pageSize == -1) return postSimpleMapper.getAllPostsOrderBy("top_most DESC, view_count DESC, post_date DESC");
                else return postSimpleMapper.getPostsOrderBy(startIndex, pageSize, "top_most DESC, view_count DESC, post_date DESC");
            }
        }else{
            if (soryBy == POST_SORT_NONE) {
                if (page == -1 && pageSize == -1) return postSimpleMapper.getTagPostsOrderBy(onlyTag, "top_most DESC");
                else return postSimpleMapper.getTagPostsWithLimitOrderBy(startIndex, pageSize, onlyTag, "top_most DESC");
            } else if (soryBy == POST_SORT_BY_NAME) {
                if (page == -1 && pageSize == -1) return postSimpleMapper.getTagPostsOrderBy("title ASC", onlyTag);
                else return postSimpleMapper.getTagPostsWithLimitOrderBy(startIndex, pageSize, "title ASC", onlyTag);
            } else if (soryBy == POST_SORT_BY_DATE) {
                if (page == -1 && pageSize == -1) return postSimpleMapper.getTagPostsOrderBy("post_date DESC", onlyTag);
                else return postSimpleMapper.getTagPostsWithLimitOrderBy(startIndex, pageSize, "post_date DESC", onlyTag);
            } else if (soryBy == POST_SORT_BY_VIEW) {
                if (page == -1 && pageSize == -1) return postSimpleMapper.getTagPostsOrderBy("view_count DESC, post_date DESC", onlyTag);
                else return postSimpleMapper.getTagPostsWithLimitOrderBy(startIndex, pageSize, "view_count ESC, post_date DESC", onlyTag);
            }
        }
        return null;
    }
    @Override
    @Cacheable(value = "blog-posts-pages-cache")
    public Page<PostSimple> getSimplePostsWithPageable(Integer page, Integer pageSize, Integer soryBy, String onlyTag, String byDate, String byClass, Integer byUser, String byStatus, Boolean noTopMost) throws NoPrivilegeException {

        Page<PostSimple> result;
        HttpServletRequest request = ContextHolderUtils.getRequest();
        //生成分页（这里性能不是非常好）
        Pageable pageable = null;
        if(soryBy==POST_SORT_NONE) {
            if(noTopMost) pageable = PageRequest.of(page, pageSize);
            else pageable = PageRequest.of(page, pageSize, new Sort(Sort.Direction.DESC, "topMost"));
        }else if(soryBy==POST_SORT_BY_NAME) {
            if(noTopMost) pageable = PageRequest.of(page, pageSize, new Sort(Sort.Direction.ASC, "title"));
            else pageable = PageRequest.of(page, pageSize, new Sort(Sort.Direction.DESC, "topMost").and(new Sort(Sort.Direction.ASC, "title")));
        }else if(soryBy==POST_SORT_BY_DATE) {
            if(noTopMost) pageable = PageRequest.of(page, pageSize, new Sort(Sort.Direction.DESC,"postDate"));
            else pageable = PageRequest.of(page, pageSize, new Sort(Sort.Direction.DESC, "topMost","postDate"));
        }else if(soryBy==POST_SORT_BY_VIEW) {
            if(noTopMost) pageable = PageRequest.of(page, pageSize, new Sort(Sort.Direction.DESC, "viewCount", "postDate"));
            else pageable = PageRequest.of(page, pageSize, new Sort(Sort.Direction.DESC, "topMost", "viewCount", "postDate"));
        }else throw new InvalidArgumentException("Invalid argument soryBy : " + soryBy);

        if("all".equals(byStatus)){

            if(byUser != 0){
                //检查是否当前读取的是自己的文章
                if(byUser != PublicAuth.authGetUseId(request).intValue()) {
                    //否则，检查用户是否有管理文章的权限
                    int authCode = PublicAuth.authCheckIncludeLevelAndPrivileges(request, User.LEVEL_WRITER, UserPrivileges.PRIVILEGE_MANAGE_ALL_ARCHIVES);
                    if(authCode < AuthCode.SUCCESS) throw new NoPrivilegeException("无权限访问用户 " + byUser + " 的数据", 403);
                }

                if(!"none".equals(onlyTag)) result = postSimpleRepository.findByAuthorIdAndTagsLike(byUser,"%-" + onlyTag + "-%", pageable);
                else if(!"0-0".equals(byDate)) result = postSimpleRepository.findByAuthorIdAndPostDateLike(byUser,byDate + "%", pageable);
                else if(!"none".equals(byClass)) result = postSimpleRepository.findByAuthorIdAndPostClassLike(byUser, byClass + ":%", pageable);
                else result = postSimpleRepository.findByAuthorId(byUser, pageable);

            }else{
                //需要有管理所有文章权限才可读取
                int authCode = PublicAuth.authCheckIncludeLevelAndPrivileges(request, User.LEVEL_WRITER, UserPrivileges.PRIVILEGE_MANAGE_ALL_ARCHIVES);
                if(authCode < AuthCode.SUCCESS) throw new NoPrivilegeException("无权限访问数据", 403);

                //读取
                if(!"none".equals(onlyTag)) result = postSimpleRepository.findByTagsLikeAndShowInList("%-" + onlyTag + "-%", true, pageable);
                else if(!"0-0".equals(byDate)) result = postSimpleRepository.findByPostDateLikeAndShowInList(byDate + "%", true, pageable);
                else if(!"none".equals(byClass)) result = postSimpleRepository.findByPostClassLikeAndShowInList(byClass + ":%", true, pageable);
                else result = postSimpleRepository.findAll(pageable);
            }
        }else{
            int byStatusVal = byStatusStrToVal(byStatus);

            if(byUser != 0){
                if(byStatusVal != Post.POST_STATUS_PUBLISH) {
                    //访问未公开的数据需要认证
                    //检查是否当前读取的是自己的文章，否则需要有管理所有文章权限才可读取当前用户才能读取
                    if (byUser != PublicAuth.authGetUseId(request).intValue()) {
                        //否则，检查用户是否有管理文章的权限
                        int authCode = PublicAuth.authCheckIncludeLevelAndPrivileges(request, User.LEVEL_WRITER, UserPrivileges.PRIVILEGE_MANAGE_ALL_ARCHIVES);
                        if (authCode < AuthCode.SUCCESS)
                            throw new NoPrivilegeException("无权限访问用户 " + byUser + " 的数据", 403);
                    }
                }

                //读取
                if(!"none".equals(onlyTag)) result = postSimpleRepository.findByStatusAndAuthorIdAndTagsLike(byStatusVal, byUser, "%-" + onlyTag + "-%", pageable);
                else if(!"0-0".equals(byDate)) result = postSimpleRepository.findByStatusAndAuthorIdAndPostDateLike(byStatusVal, byUser, byDate + "%", pageable);
                else if(!"none".equals(byClass)) result = postSimpleRepository.findByStatusAndAuthorIdAndPostClassLike(byStatusVal , byUser,byClass + ":%", pageable);
                else result = postSimpleRepository.findByStatusAndAuthorId(byStatusVal, byUser, pageable);

            }else{
                if(byStatusVal != Post.POST_STATUS_PUBLISH){
                    //访问未公开的数据需要认证
                    //需要有管理所有文章权限才可读取
                    int authCode = PublicAuth.authCheckIncludeLevelAndPrivileges(request, User.LEVEL_WRITER, UserPrivileges.PRIVILEGE_MANAGE_ALL_ARCHIVES);
                    if(authCode < AuthCode.SUCCESS) throw new NoPrivilegeException("无权限访问数据", 403);
                }

                //读取
                if(!"none".equals(onlyTag)) result = postSimpleRepository.findByStatusAndShowInListAndTagsLike(byStatusVal, true, "%-" + onlyTag + "-%", pageable);
                else if(!"0-0".equals(byDate)) result = postSimpleRepository.findByStatusAndShowInListAndPostDateLike(byStatusVal, true, byDate + "%", pageable);
                else if(!"none".equals(byClass)) result = postSimpleRepository.findByStatusAndShowInListAndPostClassLike(byStatusVal, true, byClass + ":%", pageable);
                else result = postSimpleRepository.findByStatusAndShowInList(byStatusVal, true, pageable);
            }
        }
        return result;
    }

    //删除一些文章
    @Override
    @CacheEvict(value = "blog-posts-pages-cache", allEntries = true)
    public Result deleteSomePosts(List<Integer> ids) throws NoPrivilegeException {

        HttpServletRequest request = ContextHolderUtils.getRequest();
        //检查当前用户是否有权限删除文章
        int userId = PublicAuth.authGetUseId(request);
        if(userId < AuthCode.SUCCESS) throw new NoPrivilegeException("未授权操作", 403);

        //检查用户是否有管理文章的权限
        int authCode = PublicAuth.authCheckIncludeLevelAndPrivileges(request, User.LEVEL_WRITER, UserPrivileges.PRIVILEGE_MANAGE_ALL_ARCHIVES);
        if(authCode < AuthCode.SUCCESS) {
            //无管理文章的权限，检查每一篇文章是否都所属于该用户，因为删除不是高性能操作
            //所以使用笨方法一个个检查
            for(Integer id : ids){
                //检查是否是该用户的文章，否则则抛出异常
                if(postSimpleMapper.getPostAuthorId(id) != userId)
                    throw new NoPrivilegeException("无权限删除目标数据：ID " + id, 403);
            }
        }


        //日志
        StringBuilder idStrs = new StringBuilder("[");
        for(Integer id : ids) {
            idStrs.append(",");
            idStrs.append(id);
        }
        idStrs.append("]");
        ActionLog.logUserAction("删除文章组：" + idStrs.toString(), ContextHolderUtils.getRequest());


        //写入数据库进行删除
        postSimpleRepository.deleteByIdIn(ids);
        return Result.success();
    }

    //获取一些文章统计计数参数
    @Override
    public Result getPostsStats(List<Integer> ids) {

        Map<Integer, PostStat> rs = new HashMap<>();
        for(Integer id : ids)
            rs.put(id, getPostsStats(id));
        return Result.success(rs);
    }

    @Override
    @Cacheable(value = "blog-simple-reader-cache", key = "'post_stat_'+#p0")
    public PostStat getPostsStats(Integer id) {
        return postSimpleMapper.getPostStatById(id);
    }

    //文章状态参数转数值
    private int byStatusStrToVal(String s){
        if("publish".equals(s) || "none".equals(s)) return Post.POST_STATUS_PUBLISH;
        if("draft".equals(s)) return Post.POST_STATUS_AUTOSAVE;
        if("private".equals(s)) return Post.POST_STATUS_PRIVATE;
        if(StringUtils.isInteger(s)) return Integer.parseInt(s);
        return Post.POST_STATUS_PUBLISH;
    }
}
