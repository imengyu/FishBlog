package com.dreamfish.fishblog.core.service;

import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;

public interface PostSearcherCacheService {

    ModelAndView genArchivesViewCache();
    ModelAndView genArchivesCache(String postIdOrName, HttpServletResponse response);

    ModelAndView genTagViewCache(Integer tagId, HttpServletResponse response);
    ModelAndView genDateViewCache(Integer year, Integer month, HttpServletResponse response);
    ModelAndView genClassiewCache(String classIdOrName, HttpServletResponse response);
}
