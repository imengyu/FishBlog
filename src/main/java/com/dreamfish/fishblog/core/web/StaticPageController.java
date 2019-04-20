package com.dreamfish.fishblog.core.web;

import com.dreamfish.fishblog.core.annotation.RequestAuth;
import com.dreamfish.fishblog.core.entity.User;
import com.dreamfish.fishblog.core.service.PostSearcherCacheService;
import com.dreamfish.fishblog.core.utils.Result;
import com.dreamfish.fishblog.core.utils.request.RequestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collections;


/**
 * 静态页面接口控制器
 */
@Controller
public class StaticPageController
{
    //request And response

    @Autowired
    private HttpServletRequest request;
    @Autowired
    private HttpServletResponse response;

    private static boolean seacherEngineMode = false;

    @Autowired
    private PostSearcherCacheService postSearcherCacheService;

    //Pages

    @GetMapping("/")
    public String index() {
        return "blog-index";
    }
    @GetMapping("/archives/")
    public ModelAndView archives() {
        if (seacherEngineMode || RequestUtils.checkIsSearchEngines(request)) return postSearcherCacheService.genArchivesViewCache();
        else return new ModelAndView ("blog-all");
    }
    @GetMapping("/archives/post/{idOrName}")
    public ModelAndView  viewArchive(@PathVariable("idOrName") String idOrName) {
        if (seacherEngineMode || RequestUtils.checkIsSearchEngines(request)) return postSearcherCacheService.genArchivesCache(idOrName, response);
        else return new ModelAndView ("blog-view");
    }
    @GetMapping("/archives/tag/{id}")
    public ModelAndView  viewTags(@PathVariable("id") Integer id) {
        if (seacherEngineMode || RequestUtils.checkIsSearchEngines(request)) return postSearcherCacheService.genTagViewCache(id, response);
        else return new ModelAndView ("blog-tags");
    }
    @GetMapping("/archives/month/")
    public ModelAndView  viewAllDates() {
        if (seacherEngineMode || RequestUtils.checkIsSearchEngines(request)) return postSearcherCacheService.genArchivesViewCache();
        else return new ModelAndView ("blog-all-dates");
    }
    @GetMapping("/archives/month/{year}/{month}/")
    public ModelAndView  viewDates(@PathVariable("year") Integer year, @PathVariable("month") Integer month) {
        if (seacherEngineMode || RequestUtils.checkIsSearchEngines(request)) return postSearcherCacheService.genDateViewCache(year, month, response);
        else return new ModelAndView ("blog-dates");
    }
    @GetMapping("/archives/class/{idOrName}/")
    public ModelAndView  viewClasss(@PathVariable("idOrName") String idOrName) {
        if (seacherEngineMode || RequestUtils.checkIsSearchEngines(request)) return postSearcherCacheService.genClassiewCache(idOrName, response);
        else return new ModelAndView ("blog-classes");
    }

    @GetMapping("/semode")
    @ResponseBody
    @RequestAuth(value = User.LEVEL_ADMIN)
    public Result switchSEMode(@RequestParam("switchOn") String s) {
        seacherEngineMode = s.equals("true");
        return Result.success(seacherEngineMode);
    }

    @GetMapping("baidu_verify_{k}.html")
    @ResponseBody
    public String baiduVerify(@PathVariable("k") String k){ return k; }
}
