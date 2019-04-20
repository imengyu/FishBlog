package com.dreamfish.fishblog.core.service.impl;

import com.dreamfish.fishblog.core.annotation.RequestAuth;
import com.dreamfish.fishblog.core.annotation.RequestPrivilegeAuth;
import com.dreamfish.fishblog.core.entity.PostClass;
import com.dreamfish.fishblog.core.entity.PostDate;
import com.dreamfish.fishblog.core.entity.PostTag;
import com.dreamfish.fishblog.core.entity.User;
import com.dreamfish.fishblog.core.enums.UserPrivileges;
import com.dreamfish.fishblog.core.mapper.PostClassMapper;
import com.dreamfish.fishblog.core.mapper.PostDatesMapper;
import com.dreamfish.fishblog.core.mapper.PostTagsMapper;
import com.dreamfish.fishblog.core.repository.PostClassRepository;
import com.dreamfish.fishblog.core.service.PostClassificationService;
import com.dreamfish.fishblog.core.utils.log.ActionLog;
import com.dreamfish.fishblog.core.utils.request.ContextHolderUtils;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 文章分类信息存取服务
 */
@Service
public class PostClassificationServiceImpl implements PostClassificationService {

    @Autowired
    private PostClassMapper postClassMapper;
    @Autowired
    private PostTagsMapper postTagsMapper;
    @Autowired
    private PostDatesMapper postDatesMapper;

    @Autowired
    private PostClassRepository postClassRepository;

    @Override
    @Cacheable("blog-tags-cache")
    public List<PostTag> getTags() { return postTagsMapper.getAllTags(); }
    @Override
    @Cacheable("blog-tags-cache")
    public Map<String, Object> getTags(Integer startIndex, Integer maxCount) {
        Map<String, Object> map = new HashMap<>();
        map.put("list", postTagsMapper.getTags(startIndex, maxCount));
        map.put("allCount", postTagsMapper.getTagsCount());
        return map;
    }
    @Override
    @Cacheable("blog-tags-cache")
    public List<PostTag> findTagsById(Integer id) {
        return postTagsMapper.getTagById(id);
    }

    @Override
    @CacheEvict(value = "blog-tags-cache", allEntries = true)
    public PostTag addTag(PostTag postTag) {
        postTagsMapper.addTag(postTag);
        ActionLog.logUserAction("创建标签："+postTag.getId(), ContextHolderUtils.getRequest());
        return postTag;
    }
    @Override
    @CacheEvict(value = "blog-tags-cache", allEntries = true)
    public void updateTag(PostTag postTag) {
        postTagsMapper.updateTag(postTag);
        ActionLog.logUserAction("更新标签："+postTag.getId(), ContextHolderUtils.getRequest());
    }
    @Override
    @CacheEvict(value = "blog-tags-cache", allEntries = true)
    public void deleteTag(Integer id) {
        ActionLog.logUserAction("删除标签："+id, ContextHolderUtils.getRequest());
        postTagsMapper.deleteTag(id);
    }

    @Override
    @Cacheable("blog-classes-cache")
    public List<PostClass> getClasses() { return postClassMapper.getAllClasses(); }
    @Override
    @Cacheable("blog-classes-cache")
    public Map<String, Object> getClasses(Integer startIndex, Integer maxCount) {
        Map<String, Object> map = new HashMap<>();
        map.put("list", postClassMapper.getClasses(startIndex, maxCount));
        map.put("allCount", postClassMapper.getClassesCount());
        return map;
    }
    @Cacheable("blog-classes-cache")
    @Override
    public Page<PostClass> getClassesWithPager(Integer page, Integer pageSize) {
        return postClassRepository.findAll(new PageRequest(page, pageSize));
    }
    @Cacheable("blog-classes-cache")
    @Override
    public PostClass findClassesById(Integer id) throws NotFoundException {
        PostClass result = postClassMapper.getClassesById(id);
        if(result==null) throw new NotFoundException("Not found post class : " + id);
        return result;
    }
    @Cacheable("blog-classes-cache")
    @Override
    public PostClass findClassesByUrlName(String urlName) throws NotFoundException {
        PostClass result =  postClassMapper.getClassesByUrlName(urlName);
        if(result==null) throw new NotFoundException("Not found post class : " + urlName);
        return result;
    }

    @Override
    @Cacheable("blog-dates-cache")
    public List<PostDate> getDates() { return postDatesMapper.getAllDates(); }
    @Override
    @Cacheable("blog-dates-cache")
    public Map<String, Object> getDates(Integer startIndex, Integer maxCount) {
        Map<String, Object> map = new HashMap<>();
        map.put("list", postDatesMapper.getAllDatesWithLimit(startIndex, maxCount));
        map.put("allCount", postDatesMapper.getDatesCount());
        return map;
    }

    @Override
    public PostDate findDatesById(Integer id) {
        return postDatesMapper.getDateById(id);
    }


    @Override
    @CacheEvict(value = "blog-dates-cache", allEntries = true)
    public PostDate addDate(PostDate postDate) {
        postDatesMapper.addDate(postDate);
        return postDate;
    }
    @Override
    @CacheEvict(value = "blog-dates-cache", allEntries = true)
    public void updateDate(PostDate postDate) {
        postDatesMapper.updateDate(postDate);
    }
    @Override
    @CacheEvict(value = "blog-dates-cache", allEntries = true)
    public void deleteDate(Integer id) {
        postDatesMapper.deletDate(id);
    }

    @Override
    @CacheEvict(value = "blog-classes-cache", allEntries = true)
    public PostClass addClass(PostClass postClass) {
        postClass = postClassRepository.saveAndFlush(postClass);
        ActionLog.logUserAction("创建分类："+postClass.getId(), ContextHolderUtils.getRequest());
        return postClass;
    }
    @Override
    @CacheEvict(value = "blog-classes-cache", allEntries = true)
    public void updateClass(PostClass postClass) {
        postClassRepository.saveAndFlush(postClass);
        ActionLog.logUserAction("更新分类："+postClass.getId(), ContextHolderUtils.getRequest());
    }
    @Override
    @CacheEvict(value = "blog-classes-cache", allEntries = true)
    public void deleteClass(Integer id) {
        ActionLog.logUserAction("更新分类："+id, ContextHolderUtils.getRequest());
        postClassMapper.deleteClass(id);
    }
    @Override
    @CacheEvict(value = "blog-classes-cache", allEntries = true)
    public void deleteClasses(List<Integer> ids) {
        postClassRepository.deleteByIdIn(ids);
    }
}
