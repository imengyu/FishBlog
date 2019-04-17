package com.dreamfish.fishblog.core.web;

import com.alibaba.fastjson.JSONObject;
import com.dreamfish.fishblog.core.annotation.RequestAuth;
import com.dreamfish.fishblog.core.config.ConstConfig;
import com.dreamfish.fishblog.core.entity.User;
import com.dreamfish.fishblog.core.service.StatService;
import com.dreamfish.fishblog.core.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;

@Controller
@RequestMapping(ConstConfig.API_PUBLIC)
public class StatController {

    @Autowired
    private StatService statService = null;

    @PostMapping("/stat")
    @ResponseBody
    public Result updateStat(@RequestBody @NonNull JSONObject data){
        return statService.updateStat(data);
    }

    @GetMapping("/stat")
    @RequestAuth(User.LEVEL_WRITER)
    @ResponseBody
    public Result getStatSimple(){ return statService.getStatSimple(); }


    @GetMapping("/stat/daylog")
    @RequestAuth(User.LEVEL_WRITER)
    @ResponseBody
    public Result getStatDayLog(){ return statService.getStatIpPv(); }

    @GetMapping("/stat/daylog/{pageIndex}/{pageSize}")
    @RequestAuth(User.LEVEL_WRITER)
    @ResponseBody
    public Result getStatDayLogWithPageable(
            @PathVariable("pageIndex")
            @Min(value = 0, message = "页数必须大于等于0")
                    Integer pageIndex,
            @PathVariable("pageSize")
            @Min(value = 1, message = "页大小必须大于等于1")
                    Integer pageSize){ return statService.getStatIpPv(pageIndex, pageSize); }

    @GetMapping("/stat/topPage")
    @RequestAuth(User.LEVEL_WRITER)
    @ResponseBody
    public Result getStatTopPage(@RequestParam("maxCount") Integer maxCount){ return statService.getStatTopPage(maxCount); }

    @GetMapping("/stat/topPost")
    @RequestAuth(User.LEVEL_WRITER)
    @ResponseBody
    public Result getStatTopPost(@RequestParam("maxCount") Integer maxCount){ return statService.getStatTopPost(maxCount); }

}
