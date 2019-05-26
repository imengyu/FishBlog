package com.dreamfish.fishblog.core.service;

import com.alibaba.fastjson.JSONObject;
import com.dreamfish.fishblog.core.entity.UserExtened;
import com.dreamfish.fishblog.core.utils.Result;

public interface UserService {

    String USER_ACTIVE_TOKEN_KEY = "USER11_ACTIVE";

    boolean isUserExistes(int id);
    UserExtened findUser(int id);
    UserExtened findUserByThirdId(String type, String id);
    UserExtened findUserByEmail(String email);
    Result deleteUser(int userId);
    Result addUser(UserExtened user);
    Result addUserSignUp(UserExtened user);
    void deleteUserInternal(int userId);
    UserExtened addUserInternal(UserExtened user);
    UserExtened updateUserInternal(UserExtened user);
    void updateUserId(Integer oldId, Integer newId);
    boolean activeUser(String token);
    boolean sendRepasswordMessage(String emailOrPhone);
    String getUserNameAutoById(Integer id);

    Result testUserChangePasswordToken(String token);
    Result updateUserPasswordRecover(JSONObject passwords);
    Result updateUserPassword(Integer userId, JSONObject passwords);
    Result updateUser(UserExtened user);
    Result userUpdateBan(int userId, boolean ban);
    Result userUpdatePrivilege(int userId, int newPrivilege);
    Result getUsersWithPageable(Integer pageIndex, Integer pageSize);
}