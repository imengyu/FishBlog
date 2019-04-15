package com.dreamfish.fishblog.core.service;

import com.dreamfish.fishblog.core.entity.PostSimple;
import com.dreamfish.fishblog.core.exception.NoPrivilegeException;
import com.dreamfish.fishblog.core.utils.Result;
import org.springframework.data.domain.Page;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface PostSimpleService {

    int POST_SORT_NONE = 0;
    int POST_SORT_BY_DATE = 1;
    int POST_SORT_BY_VIEW = 2;
    int POST_SORT_BY_NAME = 3;

    List<PostSimple> getSimpleWithMaxCount(Integer maxCount, Integer soryBy);
    List<PostSimple> getSimplePosts(Integer page, Integer pageSize);
    List<PostSimple> getSimplePosts(Integer page, Integer pageSize, Integer soryBy);
    List<PostSimple> getSimplePosts(Integer page, Integer pageSize, Integer soryBy, String onlyTag);
    Page<PostSimple> getSimplePostsWithPageable(Integer page, Integer pageSize, Integer soryBy, String onlyTag, String byDate, String byClass, Integer byUser, String byStatus) throws NoPrivilegeException;

    Result deleteSomePosts(List<Integer> ids) throws NoPrivilegeException;
}
