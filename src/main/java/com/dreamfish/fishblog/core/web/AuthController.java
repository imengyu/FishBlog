package com.dreamfish.fishblog.core.web;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dreamfish.fishblog.core.config.ConstConfig;
import com.dreamfish.fishblog.core.entity.User;
import com.dreamfish.fishblog.core.entity.UserExtened;
import com.dreamfish.fishblog.core.service.AuthService;
import com.dreamfish.fishblog.core.service.RedisService;
import com.dreamfish.fishblog.core.service.UserService;
import com.dreamfish.fishblog.core.utils.Result;
import com.dreamfish.fishblog.core.utils.ResultCodeEnum;
import com.dreamfish.fishblog.core.utils.StringUtils;
import com.dreamfish.fishblog.core.utils.auth.PublicAuth;
import com.dreamfish.fishblog.core.utils.request.HttpClient;
import com.dreamfish.fishblog.core.utils.request.IpUtil;
import com.dreamfish.fishblog.core.utils.response.AuthCode;
import com.dreamfish.fishblog.core.utils.request.CookieUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * 认证接口控制器
 */
@RestController
@RequestMapping(ConstConfig.API_PUBLIC_AUTH)
public class AuthController {

    @Autowired
    private HttpServletRequest request = null;
    @Autowired
    private HttpServletResponse response = null;

    @Autowired
    private AuthService authService = null;
    @Autowired
    private UserService userService = null;

    //Redis
    @Autowired
    private RedisService redisService = null;

    //开始认证 登录
    @ResponseBody
    @PostMapping(value = "", name = "开始认证")
    public Result authEntry(@RequestBody @NonNull User user) {

        String ip = IpUtil.getIpAddr(request);
        HttpSession httpSession = request.getSession();
        String passwordErrCountKey = "password_error_count_" + ip + "_" + user.getName();

        //检查密码错误次数
        Integer passwordErrCountInSession = (Integer)httpSession.getAttribute("PasswordIncorrectCount");
        Integer passwordErrCount = redisService.get(passwordErrCountKey);
        if (passwordErrCountInSession != null && passwordErrCountInSession > 3)
            return Result.failure(ResultCodeEnum.FAILED_AUTH.getCode(), "您的密码错误次数过多，请 15 分钟后再试！");
        if ((passwordErrCount != null && passwordErrCount > 3)) {
            if(passwordErrCountInSession == null || passwordErrCountInSession > 3)
                return Result.failure(ResultCodeEnum.FAILED_AUTH.getCode(), "您的密码错误次数过多，请 15 分钟后再试！");
        }

        Integer authCode = authService.authLogin(user.getName(), user.getPasswd(), request);
        if(authCode >= AuthCode.SUCCESS){

            //设置错误计数为0
            httpSession.setAttribute("PasswordIncorrectCount", 0);
            redisService.set(passwordErrCountKey, 0, 900, TimeUnit.SECONDS);

            String authToken = authService.genAuthToken(request);
            if(!StringUtils.isEmpty(authToken))
                CookieUtils.setookie(response, AuthService.AUTH_TOKEN_NAME, authToken, -1);
            return Result.success();
        }
        else {

            if (authCode != AuthCode.FAIL_USER_LOCKED) {
                //设置或增加密码错误次数
                if(passwordErrCountInSession==null) {
                    passwordErrCountInSession = 1;
                    httpSession.setAttribute("PasswordIncorrectCount", passwordErrCountInSession);
                }else{
                    passwordErrCountInSession++;
                    httpSession.setAttribute("PasswordIncorrectCount", passwordErrCountInSession);
                }
                if (passwordErrCount == null) {
                    passwordErrCount = 1;
                    redisService.set(passwordErrCountKey, passwordErrCount, 900, TimeUnit.SECONDS);
                } else {
                    passwordErrCount++;
                    redisService.set(passwordErrCountKey, passwordErrCount, 900, TimeUnit.SECONDS);
                }

                if (passwordErrCount >= 4)
                    return Result.failure(ResultCodeEnum.FAILED_AUTH.getCode(), "您的密码错误次数过多，请 15 分钟后再试！", String.valueOf(authCode));
                else return Result.failure(ResultCodeEnum.FAILED_AUTH.getCode(), "您还可以尝试 " + (4 - passwordErrCount) + " 次", String.valueOf(authCode));

            }else return Result.failure(ResultCodeEnum.FAILED_AUTH, authCode.toString());
        }
    }

    //检测认证状态
    @ResponseBody
    @GetMapping(value = "/auth-test-user-id", name = "检测认证状态")
    public Result authTestUserId() {
        Integer authCode = authService.checkUserAuth(request);
        if(authCode >= AuthCode.SUCCESS) return Result.success(PublicAuth.authGetUseId(request));
        else return Result.failure(ResultCodeEnum.FAILED_AUTH, String.valueOf(authCode));
    }
    //检测认证状态
    @ResponseBody
    @GetMapping(value = "/auth-test", name = "检测认证状态")
    public Result authTest() {
        Integer authCode = authService.checkUserAuth(request);
        if(authCode >= AuthCode.SUCCESS) return Result.success(authService.authGetAuthedUserInfo(request));
        else return Result.failure(ResultCodeEnum.FAILED_AUTH, String.valueOf(authCode));
    }
    //结束认证 退出
    @ResponseBody
    @GetMapping(value = "/auth-end", name = "结束认证")
    public Result authEnd(@RequestParam(value = "redirect_uri", required = false) String redirect_uri) throws IOException {
        Integer authCode = authService.checkUserAuth(request);
        CookieUtils.setookie(response, AuthService.AUTH_TOKEN_NAME, "", 0);
        authService.authClear(request);
        if(!StringUtils.isBlank(redirect_uri)){
            response.sendRedirect(redirect_uri);
            return Result.success();
        }else {
            if (authCode >= AuthCode.SUCCESS) return Result.success();
            else return Result.failure(ResultCodeEnum.FAILED_AUTH, String.valueOf(authCode));
        }
    }

    //Github 登录
    //------------------------------------------------------------------------------

    //登录验证成功回调
    @GetMapping(value = "/auth-github", name = "Github 登录验证成功回调")
    public void authGithubAuthSuccess(@RequestParam("redirect_uri") String redirect_uri, @RequestParam("access_token") String access_token) throws IOException {
        String result = HttpClient.sendGetRequest("https://api.github.com/user?access_token=" + access_token, null, new HttpHeaders());
        if(!StringUtils.isEmpty(result)){

            JSONObject jsonobject = JSON.parseObject(result);
            String id = jsonobject.getString("id");

            //Get user info and update\add
            String avatar_url = jsonobject.getString("avatar_url");
            String blog = jsonobject.getString("blog");
            String email = jsonobject.getString("email");
            String name = jsonobject.getString("name");
            String loginName = jsonobject.getString("login");
            if(StringUtils.isEmpty(name)) name = loginName;

            //Find by thid id
            UserExtened oldUser = userService.findUserByThirdId("github", id);
            if(oldUser == null) {
                //Add new user
                oldUser = new UserExtened();
                oldUser.setFriendlyName(name);
                oldUser.setThirdId("github_" + id);
                oldUser.setEmail(email);

                oldUser.setHeadimg(avatar_url);
                oldUser.setHome(blog);
                oldUser.setName(loginName);
                oldUser.setLevel(User.LEVEL_GUEST);
                oldUser.setUserFrom("github");
                oldUser.setOldLevel(User.LEVEL_GUEST);

                int oldId = userService.addUserInternal(oldUser).getId();
                userService.updateUserId(oldId, Integer.parseInt(id));
            }
            else {
                //Update user
                boolean needUpdate = false;

                if(oldUser.getLevel() == User.LEVEL_LOCKED){
                    response.sendRedirect("/user/baned/");
                    return;
                }

                if((StringUtils.isEmpty(oldUser.getEmail()) && StringUtils.isEmpty(email))
                        || !oldUser.getEmail().equals(email)) { oldUser.setEmail(email); needUpdate=true; }
                if((StringUtils.isEmpty(oldUser.getHome()) && StringUtils.isEmpty(blog))
                        || !oldUser.getHome().equals(blog)) { oldUser.setHome(blog); needUpdate=true; }
                if((StringUtils.isEmpty(oldUser.getFriendlyName()) && StringUtils.isEmpty(name))
                        || !oldUser.getFriendlyName().equals(name)) { oldUser.setFriendlyName(name); needUpdate=true; }
                if((StringUtils.isEmpty(oldUser.getHeadimg()) && StringUtils.isEmpty(avatar_url))
                        || !oldUser.getHeadimg().equals(avatar_url)) { oldUser.setHeadimg(avatar_url); needUpdate=true; }

                if(needUpdate) userService.updateUserInternal(oldUser);
            }

            //Set login session
            HttpSession session = request.getSession();
            session.setMaxInactiveInterval(AuthService.AUTH_TOKEN_GUEST_EXPIRE_TIME);
            session.setAttribute("currentLoggedUserId", Integer.parseInt(id));
            session.setAttribute("currentLoggedUserName", loginName);
            session.setAttribute("currentLoggedUserLevel", User.LEVEL_GUEST);
            session.setAttribute("currentLoggedUserPrivileges", 0);

            //Gen lgin cookie
            String authToken = authService.genAuthToken(request, AuthService.AUTH_TOKEN_GUEST_EXPIRE_TIME);
            if(!StringUtils.isEmpty(authToken))
                CookieUtils.setookie(response, AuthService.AUTH_TOKEN_NAME, authToken, AuthService.AUTH_TOKEN_GUEST_EXPIRE_TIME);

            //Jump to old page
            if(redirect_uri.equals("user")) response.sendRedirect("/user/" + id + "/");
            else response.sendRedirect(redirect_uri);
            return;
        }
        response.sendRedirect("/auth?error=BadRequest");
    }
    //Github 登录验证回调
    @GetMapping(value = "/githubAuthCallback", name = "Github 登录验证错误回调")
    public Result authGithubAuthCallbackError(
            @RequestParam("code") String code,
            @RequestParam(value = "redirect_uri", required = false) String redirect_uri,
            @RequestParam(value = "error", required = false) String error,
            @RequestParam(value = "error_description", required = false) String error_description
    ) throws IOException {
        if(StringUtils.isEmpty(redirect_uri) || !StringUtils.isEmpty(error)) return Result.failure("400", error, error_description);
        else {

            String param = "client_id=d31012693b9ba3773cde&client_secret=9dad579e417de46aed7ceecc091545f72473d7e1"
                    + "&code=" + code;
            String result = HttpClient.sendGetRequest("https://github.com/login/oauth/access_token?" + param, null, new HttpHeaders());
            if(StringUtils.startWith(result, "access_token="))
                response.sendRedirect("/" + ConstConfig.API_PUBLIC_AUTH + "/auth-github?redirect_uri=" + redirect_uri + "&" + result);
            else  response.sendRedirect("/auth?" + result);

            return Result.success();
        }
    }

}