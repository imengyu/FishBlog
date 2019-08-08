package com.dreamfish.fishblog.core.web;

import com.dreamfish.fishblog.core.entity.Post;
import com.dreamfish.fishblog.core.entity.PostSimple;
import com.dreamfish.fishblog.core.service.PostService;
import com.dreamfish.fishblog.core.service.PostSimpleService;
import com.dreamfish.fishblog.core.utils.Result;
import com.dreamfish.fishblog.core.utils.StringUtils;
import com.dreamfish.fishblog.core.utils.encryption.Base64Utils;
import org.pegdown.PegDownProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;


import java.util.List;

@Controller
@RequestMapping("seo")
public class SeoPagesController {

    @Value("${fishblog.fish-front-post-address}")
    private String frontAddress;
    @Autowired
    private PostSimpleService postSimpleService;
    @Autowired
    private PostService postService;

    @GetMapping("/")
    public String getSeoIndex() { return "seo/index"; }

    @GetMapping("/archives")
    public ModelAndView getSeoArchives(){
        ModelAndView view = new ModelAndView("seo/archives");

        List<PostSimple> posts = postSimpleService.getSimpleWithMaxCount(64, PostSimpleService.POST_SORT_BY_VIEW);

        for(PostSimple post : posts){
            String link = frontAddress + "/" + (StringUtils.isBlank(post.getUrlName()) ? String.valueOf(post.getId())
                    : post.getUrlName());
            post.setLink(link);
        }

        view.addObject("posts", posts);

        return view;
    }

    @GetMapping("/archives/month")
    public ModelAndView getSeoArchivesMonth(){
        ModelAndView view = new ModelAndView("seo/archives");

        List<PostSimple> posts = postSimpleService.getSimpleWithMaxCount(64, PostSimpleService.POST_SORT_BY_DATE);

        for(PostSimple post : posts){
            String link = frontAddress + "/" + (StringUtils.isBlank(post.getUrlName()) ? String.valueOf(post.getId())
                    : post.getUrlName());
            post.setLink(link);
        }

        view.addObject("posts", posts);

        return view;
    }

    @GetMapping("/archives/post/{postIdOrUrlName}")
    public ModelAndView getSeoPost(@PathVariable("postIdOrUrlName") String postIdOrUrlName) {

        ModelAndView view = null;

        Result rs = postService.findPostWithIdOrUrlName(postIdOrUrlName, false);
        if(rs.isSuccess()){
            Post post = (Post)rs.getData();
            String sourceContent = Base64Utils.decode(post.getContent());

            //Covert md to html
            if("markdown".equals(post.getType())) {
                PegDownProcessor pdp = new PegDownProcessor(Integer.MAX_VALUE);
                sourceContent = pdp.markdownToHtml(sourceContent);
            }

            view = new ModelAndView("seo/post");
            view.addObject("post", post);
            view.addObject("post_content", sourceContent);

        }else{
            view = new ModelAndView("seo/error");
        }
        return view;
    }

}
