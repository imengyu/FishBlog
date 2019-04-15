package com.dreamfish.fishblog.core.service;

import com.dreamfish.fishblog.core.entity.UserExtened;
import com.dreamfish.fishblog.core.utils.Result;

import javax.servlet.http.HttpServletRequest;

public interface UserService {

    boolean isUserExistes(int id);
    UserExtened findUser(int id);
    Result deleteUser(int userId);
    Result addUser(UserExtened user);
    void deleteUserInternal(int userId);
    UserExtened addUserInternal(UserExtened user);
    UserExtened updateUserInternal(UserExtened user);
    void updateUserId(Integer oldId, Integer newId);

    Result updateUser(UserExtened user);
    Result userUpdateBan(int userId, boolean ban);
    Result userUpdatePrivilege(int userId, int newPrivilege);
    Result getUsersWithPageable(Integer pageIndex, Integer pageSize);
}