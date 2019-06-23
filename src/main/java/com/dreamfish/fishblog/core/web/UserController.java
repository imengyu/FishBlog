package com.dreamfish.fishblog.core.web;

import com.alibaba.fastjson.JSONObject;
import com.dreamfish.fishblog.core.annotation.RequestAuth;
import com.dreamfish.fishblog.core.annotation.RequestPrivilegeAuth;
import com.dreamfish.fishblog.core.config.ConstConfig;
import com.dreamfish.fishblog.core.entity.User;
import com.dreamfish.fishblog.core.entity.UserExtened;
import com.dreamfish.fishblog.core.enums.UserPrivileges;
import com.dreamfish.fishblog.core.service.MediaStorageService;
import com.dreamfish.fishblog.core.service.UserService;
import com.dreamfish.fishblog.core.utils.Result;
import com.dreamfish.fishblog.core.utils.ResultCodeEnum;
import com.dreamfish.fishblog.core.utils.StringUtils;
import com.dreamfish.fishblog.core.utils.auth.PublicAuth;
import com.dreamfish.fishblog.core.utils.request.ContextHolderUtils;
import com.dreamfish.fishblog.core.utils.response.AuthCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.IOException;

@RequestMapping(ConstConfig.API_PUBLIC)
@Controller
public class UserController {

    @Autowired
    private UserService userService = null;
    @Autowired
    private MediaStorageService mediaStorageService = null;

    //获取单个用户信息
    @GetMapping("/user/{userId}")
    @ResponseBody
    public Result getUser(@PathVariable("userId") String userId) {
        int userIdInt = 0;
        if("0".equals(userId) || "current".equals(userId)) {
            int currentUserId = PublicAuth.authGetUseId(ContextHolderUtils.getRequest());
            if(currentUserId >= AuthCode.SUCCESS)
                userIdInt = currentUserId;
            else return Result.failure(ResultCodeEnum.NOT_FOUNT);
        }else if("admin".equals(userId)) userIdInt = 1;
        else if(StringUtils.isInteger(userId)) userIdInt = Integer.parseInt(userId);
        else return Result.failure(ResultCodeEnum.BAD_REQUEST.getCode(), "userId 非数字");

        UserExtened user = userService.findUser(userIdInt);
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
                                       Integer pageSize,
                           @RequestParam(value = "includeTourist", required = false, defaultValue = "false")
                                       Boolean includeTourist
    ) {
        return userService.getUsersWithPageable(pageIndex, pageSize, includeTourist);
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

    //添加新用户
    @PostMapping("/users/sign-up")
    @ResponseBody
    public Result addUserSignUp(@RequestBody UserExtened user){ return userService.addUserSignUp(user); }

    //删除用户
    @DeleteMapping("/user/{userId}")
    @ResponseBody
    @RequestAuth(value = User.LEVEL_WRITER)
    @RequestPrivilegeAuth(value = UserPrivileges.PRIVILEGE_MANAGE_USERS)
    public Result deleteUser(@PathVariable("userId") @NonNull Integer userId){ return userService.deleteUser(userId); }


    //用户修改
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

    //用户修改密码
    @PostMapping("/user/{userId}/password")
    @ResponseBody
    public Result updateUserPassword(@PathVariable("userId") Integer userId, @RequestBody JSONObject user){ return userService.updateUserPassword(userId, user); }

    //用户找回密码时修改密码
    @PostMapping("/user/x/password")
    @ResponseBody
    public Result updateUserPasswordRecover(@RequestBody JSONObject user){ return userService.updateUserPasswordRecover(user); }
    //用户修改密码
    @GetMapping("/user/x/password/token-test")
    @ResponseBody
    public Result testUserChangePasswordToken(@RequestParam("token") String token){ return userService.testUserChangePasswordToken(token); }

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
    @RequestPrivilegeAuth(value = (UserPrivileges.PRIVILEGE_MANAGE_USERS & UserPrivileges.PRIVILEGE_GAINT_PRIVILEGE))
    public Result setUserPrivilege(@PathVariable("userId") Integer userId, @RequestBody JSONObject data) {
        return userService.userUpdatePrivilege(userId, data.getInteger("privilege"));
    }

    //设置用户用户组
    @PostMapping("/user/{userId}/level")
    @ResponseBody
    @RequestAuth(value = User.LEVEL_WRITER)
    @RequestPrivilegeAuth(value = UserPrivileges.PRIVILEGE_MANAGE_USERS)
    public Result setUserLevel(@PathVariable("userId") Integer userId,  @RequestBody JSONObject data) {
        return userService.userUpdateLevel(userId,  data.getInteger("level"));
    }

    //上传用户头像
    @PostMapping("/user/{userId}/head")
    @ResponseBody
    @RequestAuth(User.LEVEL_WRITER)
    public Result uploadUserHeadImage(
            @RequestParam(value = "file") @NotNull MultipartFile imageFile,
            @PathVariable("userId") Integer userId) throws IOException {
        return mediaStorageService.uploadImageForUserHead(imageFile, userId);
    }

    @GetMapping("/user/exists")
    @ResponseBody
    public Result getUserExistsByEmail(@RequestParam("k") String email){ return Result.success(userService.isUserExistsByEmail(email)); }

    @PostMapping("/user/recoverPassword")
    @ResponseBody
    public Result sendUserRecoverPassword(@RequestBody JSONObject data){
        userService.sendRepasswordMessage(data.getString("k"));
        return Result.success();
    }

    @PostMapping("/user/active")
    @ResponseBody
    public Result activeUser(@RequestBody JSONObject data){
        return Result.success(userService.activeUser(data.getString("k")));
    }
}
