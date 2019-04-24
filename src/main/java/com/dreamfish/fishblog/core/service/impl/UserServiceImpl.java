package com.dreamfish.fishblog.core.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.dreamfish.fishblog.core.entity.User;
import com.dreamfish.fishblog.core.entity.UserExtened;
import com.dreamfish.fishblog.core.mapper.UserMapper;
import com.dreamfish.fishblog.core.repository.UserRepository;
import com.dreamfish.fishblog.core.service.UserService;
import com.dreamfish.fishblog.core.utils.encryption.AESUtils;
import com.dreamfish.fishblog.core.utils.log.ActionLog;
import com.dreamfish.fishblog.core.utils.request.ContextHolderUtils;
import com.dreamfish.fishblog.core.utils.Result;
import com.dreamfish.fishblog.core.utils.ResultCodeEnum;
import com.dreamfish.fishblog.core.utils.StringUtils;
import com.dreamfish.fishblog.core.utils.auth.PublicAuth;
import com.dreamfish.fishblog.core.utils.response.AuthCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

import static com.dreamfish.fishblog.core.service.AuthService.AUTH_PASSWORD_KEY;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper = null;
    @Autowired
    private UserRepository userRepository = null;


    /**
     * 检查用户是否存在
     * @param id 用户id
     * @return 返回用户是否存在
     */
    @Override
    public boolean isUserExistes(int id) {
        Integer i = userMapper.isUserIdExists(id);
        return i != null && i > 0;
    }

    /**
     * 根据用户 ID 获取用户实体
     * @param id 用户 ID
     * @return 用户实体
     */
    @Override
    @Cacheable(value = "blog-user-cache", key = "'user_full_'+#p0")
    public UserExtened findUser(int id) {
        return userMapper.findFullById(id);
    }

    @Override
    public UserExtened findUserByThirdId(String type, String id) {
        return userMapper.findFullByThirdId(type + "_" + id);
    }

    /**
     * 带认证删除用户（公开需认证）
     * @param userId 用户 ID
     * @return 返回请求结果
     */
    @Override
    public Result deleteUser(int userId) {

        if (!userRepository.existsById(userId))
            return Result.failure(ResultCodeEnum.NOT_FOUNT);
        if(userMapper.getUserLevelById(userId) <= User.LEVEL_ADMIN)
            return Result.failure(ResultCodeEnum.UNAUTHORIZED.getCode(),"无权限删除指定用户");
        if(userId == PublicAuth.authGetUseId(ContextHolderUtils.getRequest()))
            return Result.failure(ResultCodeEnum.UNAUTHORIZED.getCode(),"无法删除自己");

        //日志
        ActionLog.logUserAction("注销用户：" + userId, ContextHolderUtils.getRequest());

        deleteUserInternal(userId);
        return Result.success();
    }

    /**
     * 添加新用户（公开需认证）
     * @param user 用户实体
     * @return 返回新添加的用户实体信息
     */
    @Override
    public Result addUser(UserExtened user) {

        String userName = user.getName();
        String password = user.getPasswd();

        //Force set value
        user.setOldLevel(User.LEVEL_WRITER);
        user.setLevel(User.LEVEL_WRITER);
        user.setUserFrom("here");
        user.setPrivilege(0);

        if(StringUtils.isBlank(userName) || StringUtils.isBlank(password))
            return Result.failure(ResultCodeEnum.BAD_REQUEST.getCode(),"用户名或密码不能为空");

        if (userRepository.existsByName(userName))
            return Result.failure(ResultCodeEnum.FAILED_RES_ALREADY_EXIST.getCode(),"指定用户名已存在");

        user.setPasswd(AESUtils.encrypt(password + "$" + userName, AUTH_PASSWORD_KEY));

        UserExtened newUser = addUserInternal(user);
        //日志
        ActionLog.logUserAction("新建用户：" + newUser.getId() + " (" + newUser.getFriendlyName() + ")", ContextHolderUtils.getRequest());
        return Result.success(newUser);
    }

    /**
     * 更新用户封禁状态（公开需认证）
     * @param userId 用户 ID
     * @param ban 是否封禁
     * @return 结果数据
     */
    @Override
    public Result userUpdateBan(int userId, boolean ban) {

        if(ban) {
            if(userMapper.getUserLevelById(userId) <= User.LEVEL_ADMIN)
                return Result.failure(ResultCodeEnum.UNAUTHORIZED.getCode(),"无权限对指定用户操作");
            if(userId == PublicAuth.authGetUseId(ContextHolderUtils.getRequest()))
                return Result.failure(ResultCodeEnum.UNAUTHORIZED.getCode(),"无法对自己进行操作");

            ActionLog.logUserAction("封禁用户：" + userId, ContextHolderUtils.getRequest());

            userMapper.updateUserLevel(userId, User.LEVEL_LOCKED);
        }
        else {
            ActionLog.logUserAction("解封用户：" + userId, ContextHolderUtils.getRequest());

            userMapper.updateUserLevelSetToOld(userId);
        }
        return Result.success();
    }

    /**
     * 更新用户权限（公开需认证）
     * @param userId 用户 ID
     * @param newPrivilege 用户新权限
     * @return 结果数据
     */
    @Override
    public Result userUpdatePrivilege(int userId, int newPrivilege) {

        //认证
        HttpServletRequest request = ContextHolderUtils.getRequest();
        if(PublicAuth.authCheckIncludeLevelAndPrivileges(request, User.LEVEL_WRITER, newPrivilege) < AuthCode.SUCCESS)
            return Result.failure(ResultCodeEnum.FORIBBEN.getCode(),"当前用户无权限赋予其他用户权限");
        if(userId == PublicAuth.authGetUseId(ContextHolderUtils.getRequest()))
            return Result.failure(ResultCodeEnum.UNAUTHORIZED.getCode(),"无法对自己进行操作");

        if(userMapper.getUserLevelById(userId) <= User.LEVEL_ADMIN)
            return Result.failure(ResultCodeEnum.UNAUTHORIZED.getCode(),"无权限对指定用户操作");

        ActionLog.logUserAction("更新用户权限：" + userId + " 新权限：" + newPrivilege, ContextHolderUtils.getRequest());

        userMapper.updateUsePrivilege(userId, newPrivilege);
        return Result.success();
    }


    @Override
    @CacheEvict(value = "blog-user-cache", key = "'user_full_'+#p0")
    public void deleteUserInternal(int userId) { userRepository.deleteById(userId); }
    @Override
    @CacheEvict(value = "blog-user-cache", key = "'user_full_'+#p0.id")
    public UserExtened addUserInternal(UserExtened user) { return userRepository.saveAndFlush(user); }
    @Override
    @CacheEvict(value = "blog-user-cache", key = "'user_full_'+#p0")
    public UserExtened updateUserInternal(UserExtened user) { return userRepository.saveAndFlush(user); }

    /**
     * 更新 用户 ID
     * @param oldId 用户 ID
     * @param newId 用户新 ID
     */
    @Override
    public void updateUserId(Integer oldId, Integer newId) { userMapper.updateUserId(oldId, newId); }

    /**
     * 更新 用户密码
     * @param passwords 密码参数
     * @return 返回是否成功
     */
    @Override
    public Result updateUserPassword(Integer userId, JSONObject passwords) {

        //验证是否是当前用户
        HttpServletRequest request = ContextHolderUtils.getRequest();
        Integer currentUserId = PublicAuth.authGetUseId(request);
        if(currentUserId.intValue() != userId)
            return Result.failure(ResultCodeEnum.UNAUTHORIZED.getCode(), "试图执行未授权操作");

        String userName = userMapper.getUserNameById(userId);
        String oldPassword = AESUtils.encrypt(passwords.getString("oldPassword") + "$" + userName, AUTH_PASSWORD_KEY);
        String newPassword = AESUtils.encrypt(passwords.getString("newPassword") + "$" + userName, AUTH_PASSWORD_KEY);

        if(StringUtils.isBlank(oldPassword) || StringUtils.isBlank(newPassword))
            return Result.failure(ResultCodeEnum.BAD_REQUEST.getCode(), "密码参数为空");

        String oldPasswordReal = userMapper.getUserPasswordById(userId);
        if(!oldPassword.equals(oldPasswordReal))
            return Result.failure(ResultCodeEnum.FAILED_AUTH.getCode(), "旧密码错误");

        userMapper.updateUserPassword(userId, newPassword);
        return Result.success();
    }

    /**
     * 更新用户信息
     * @param user
     * @return
     */
    @Override
    @CacheEvict(value = "blog-user-cache", key = "'user_full_'+#p0.id")
    public Result updateUser(UserExtened user) {

        HttpServletRequest request = ContextHolderUtils.getRequest();
        int userID = PublicAuth.authGetUseId(request);
        if(userID < AuthCode.SUCCESS) return Result.failure(ResultCodeEnum.UNAUTHORIZED);
        if(userID != user.getId()) return Result.failure(ResultCodeEnum.FORIBBEN.getCode(), "当前用户无法修改其他用户个人信息");

        UserExtened userOld = userMapper.findFullById(user.getId());
        if(userOld == null) return Result.failure(ResultCodeEnum.NOT_FOUNT.getCode(), "未找到指定用户");

        user.setPrivilege(userOld.getPrivilege());
        user.setLevel(userOld.getLevel());
        user.setOldLevel(userOld.getOldLevel());
        user.setUserFrom(userOld.getUserFrom());
        user.setPasswd(userOld.getPasswd());
        user.setName(userOld.getName());

        return Result.success(updateUserInternal(user));
    }

    /**
     * 获取所有用户（带分页）（公开需认证）
     * @param pageIndex 页码
     * @param pageSize 页大小
     * @return 分页结果数据
     */
    @Override
    public Result getUsersWithPageable(Integer pageIndex, Integer pageSize) {
        return Result.success(userRepository.findAll(new PageRequest(pageIndex, pageSize)));
    }
}
