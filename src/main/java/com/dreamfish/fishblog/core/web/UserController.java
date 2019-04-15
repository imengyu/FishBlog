package com.dreamfish.fishblog.core.web;

import com.alibaba.fastjson.JSONObject;
import com.dreamfish.fishblog.core.annotation.RequestAuth;
import com.dreamfish.fishblog.core.annotation.RequestPrivilegeAuth;
import com.dreamfish.fishblog.core.config.ConstConfig;
import com.dreamfish.fishblog.core.entity.User;
import com.dreamfish.fishblog.core.entity.UserExtened;
import com.dreamfish.fishblog.core.enums.UserPrivileges;
import com.dreamfish.fishblog.core.service.UserService;
import com.dreamfish.fishblog.core.utils.Result;
import com.dreamfish.fishblog.core.utils.ResultCodeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;

@RequestMapping(ConstConfig.API_PUBLIC)
@Controller
public class UserController {

    @Autowired
    private UserService userService = null;

    //获取单个用户信息
    @GetMapping("/user/{userId}")
    @ResponseBody
    public Result getUser(@PathVariable("userId") Integer userId) {
        UserExtened user = userService.findUser(userId);
        if(user!=null) return Result.success(user);
        else return Result.failure(ResultCodeEnum.NOT_FOUNT);
    }

    //获取所有用户带分页
    @GetMapping("/users/{pageIndex}/{pageSize}")
    @ResponseBody
    @RequestAuth(value = User.LEVEL_WRITER)
    @RequestPrivilegeAuth(value = UserPrivileges.PRIVILEGE_MANAGE_USERS)
    public Result getUsers(@PathVariable("pageIndex")
                               @Min(value = 0, message = "页数必须大于等于0")
                                       Integer pageIndex,
                           @PathVariable("pageSize")
                               @Min(value = 1, message = "页大小必须大于等于1")
                                       Integer pageSize) {
        return userService.getUsersWithPageable(pageIndex, pageSize);
    }
    //删除部分用户
    @DeleteMapping("/users")
    @ResponseBody
    public Result deleteUsers(){
        return Result.failure(ResultCodeEnum.NOT_IMPLEMENTED);
    }

    //添加新用户
    @PostMapping("/users")
    @ResponseBody
    @RequestAuth(value = User.LEVEL_WRITER)
    @RequestPrivilegeAuth(value = UserPrivileges.PRIVILEGE_MANAGE_USERS)
    public Result addUser(@RequestBody UserExtened user){ return userService.addUser(user); }

    //删除用户
    @DeleteMapping("/user/{userId}")
    @ResponseBody
    @RequestAuth(value = User.LEVEL_WRITER)
    @RequestPrivilegeAuth(value = UserPrivileges.PRIVILEGE_MANAGE_USERS)
    public Result deleteUser(@PathVariable("userId") @NonNull Integer userId){ return userService.deleteUser(userId); }


    //用户信息修改
    @PutMapping("/user/{userId}")
    @ResponseBody
    public Result updateUser(@RequestBody UserExtened user){ return userService.updateUser(user); }

    //解封用户
    @PostMapping("/user/{userId}/ban")
    @ResponseBody
    @RequestAuth(value = User.LEVEL_WRITER)
    @RequestPrivilegeAuth(value = UserPrivileges.PRIVILEGE_MANAGE_USERS)
    public Result banUser(@PathVariable("userId") Integer userId) {
        return userService.userUpdateBan(userId, true);
    }

    //封禁用户
    @PostMapping("/user/{userId}/unban")
    @ResponseBody
    @RequestAuth(value = User.LEVEL_WRITER)
    @RequestPrivilegeAuth(value = UserPrivileges.PRIVILEGE_MANAGE_USERS)
    public Result unbanUser(@PathVariable("userId") Integer userId) {
        return userService.userUpdateBan(userId, false);
    }

    //设置用户权限
    @PostMapping("/user/{userId}/privilege")
    @ResponseBody
    @RequestAuth(value = User.LEVEL_WRITER)
    @RequestPrivilegeAuth(value = UserPrivileges.PRIVILEGE_MANAGE_USERS)
    public Result setUserPrivilege(@PathVariable("userId") Integer userId, @RequestBody JSONObject privilege) {
        return userService.userUpdatePrivilege(userId, privilege.getInteger("privilege"));
    }


}
