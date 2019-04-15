package com.dreamfish.fishblog.core.service.impl;

import com.dreamfish.fishblog.core.entity.PostAbstract;
import com.dreamfish.fishblog.core.entity.PostUrl;
import com.dreamfish.fishblog.core.mapper.PostMapper;
import com.dreamfish.fishblog.core.service.PostSearcherCacheService;
import com.dreamfish.fishblog.core.utils.StringUtils;
import com.dreamfish.fishblog.core.utils.encryption.Base64Utils;
import com.dreamfish.fishblog.core.utils.request.RequestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Service
public class PostSearcherCacheServiceImpl implements PostSearcherCacheService {

    @Autowired
    private PostMapper postMapper;

    @Override
    public ModelAndView genArchivesViewCache() {

        ModelAndView view = new ModelAndView("blog-searcher-allposts");
        view.addObject("archives", findAbstractTitles());
        return view;
    }

    @Override
    public ModelAndView genArchivesCache(String postIdOrName, HttpServletResponse response) {
        ModelAndView view = new ModelAndView("blog-searcher-simple");

        PostAbstract postAbstract = getPostAbstract(postIdOrName);


        if(postAbstract!=null) {
            view.addObject("title", postAbstract.getTitle());
            view.addObject("keywords", postAbstract.getKeywords());
            view.addObject("description", postAbstract.getPreviewText());
            view.addObject("body", Base64Utils.decode(postAbstract.getContent()));
        }
        else response.setStatus(404);
        return view;
    }

    @Override
    public ModelAndView genTagViewCache(Integer tagId, HttpServletResponse response) {
        ModelAndView view = new ModelAndView("blog-searcher-simple-posts");
        view.addObject("title", "文章标签");
        view.addObject("archives", findAbstractTitlesWithTag(tagId));
        return view;
    }

    @Override
    public ModelAndView genDateViewCache(Integer year, Integer month, HttpServletResponse response) {
        ModelAndView view = new ModelAndView("blog-searcher-simple-posts");
        view.addObject("archives", findAbstractTitlesWithDate(year, month));
        view.addObject("title", "文章归档");
        return view;
    }

    @Override
    public ModelAndView genClassiewCache(String classIdOrName, HttpServletResponse response) {
        ModelAndView view = new ModelAndView("blog-searcher-simple-posts");
        view.addObject("archives", findAbstractTitlesWithClass(classIdOrName));
        view.addObject("title", "文章分类");
        return view;
    }


    private List<PostUrl> reallocPostUrlNames(List<PostUrl> old){
        for(PostUrl p : old){
            if(!StringUtils.isBlank(p.urlName)){
                p.setUrlName(p.urlName.replace("+", "%20"));
            }
        }
        return old;
    }
    @Cacheable("blog-bot-cache")
    private List<PostUrl> findAbstractTitlesWithClass(String classIdOrName){
        return reallocPostUrlNames(postMapper.findAbstractTitlesWithClass(classIdOrName, 32));
    }
    @Cacheable("blog-bot-cache")
    private List<PostUrl> findAbstractTitlesWithDate(Integer year, Integer month){
        return reallocPostUrlNames(postMapper.findAbstractTitlesWithDate(String.format("%02d", year), String.format("%02d", month), 32));
    }
    @Cacheable("blog-bot-cache")
    private List<PostUrl> findAbstractTitlesWithTag(Integer tagId){
        return reallocPostUrlNames(postMapper.findAbstractTitlesWithTag(tagId, 32));
    }
    @Cacheable("blog-bot-cache")
    private List<PostUrl> findAbstractTitles(){
        return reallocPostUrlNames(postMapper.findAbstractTitles(64));
    }
    @Cacheable("blog-bot-cache")
    private PostAbstract getPostAbstract(String postIdOrName) {
        if(StringUtils.isInteger(postIdOrName)) return postMapper.findAbstractById(Integer.parseInt(postIdOrName));
        else return postMapper.findAbstractByUrlName(RequestUtils.encoderURLString(postIdOrName));
    }

}

