package com.dreamfish.fishblog.core.service;


import com.dreamfish.fishblog.core.entity.PostClass;
import com.dreamfish.fishblog.core.entity.PostDate;
import com.dreamfish.fishblog.core.entity.PostTag;
import com.dreamfish.fishblog.core.utils.response.IncludeSizeListResult;
import javassist.NotFoundException;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

public interface PostClassificationService {

    List<PostTag> getTags();
    Map<String, Object> getTags(Integer startIndex, Integer maxCount);
    List<PostTag> findTagsById(Integer id);

    PostTag addTag(PostTag postTag);
    void updateTag(PostTag postTag);
    void deleteTag(Integer id);

    List<PostClass> getClasses();
    Map<String, Object> getClasses(Integer startIndex, Integer maxCount);
    Page<PostClass> getClassesWithPager(Integer page, Integer pageSize);
    PostClass findClassesById(Integer id) throws NotFoundException;
    PostClass findClassesByUrlName(String urlName) throws NotFoundException;

    List<PostDate> getDates();
    Map<String, Object> getDates(Integer startIndex, Integer maxCount);
    List<PostDate> findDatesById(Integer id);

    PostDate addDate(PostDate postDate);
    void updateDate(PostDate postDate);
    void deleteDate(Integer id);

    PostClass addClass(PostClass postClass);
    void updateClass(PostClass postClass);
    void deleteClass(Integer id);
    void deleteClasses(List<Integer> ids);
}
