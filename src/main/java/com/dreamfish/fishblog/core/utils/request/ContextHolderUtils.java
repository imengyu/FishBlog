package com.dreamfish.fishblog.core.utils.request;

import com.dreamfish.fishblog.core.utils.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

public class ContextHolderUtils {
    /**
     * SpringMvc下获取request
     *
     * @return
     */
    public static HttpServletRequest getRequest() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        return request;

    }

    /**
     * SpringMvc下获取session
     *
     * @return
     */
    public static HttpSession getSession() {
        HttpServletRequest request = getRequest();
        String tempSessionId = request.getParameter("sessionId");
        HttpSession session = request.getSession();
        String sessionId = session.getId();
        if(!StringUtils.isBlank(tempSessionId) && !tempSessionId.equals(sessionId)){
            sessionId = tempSessionId;
            if(sessionMap.containsKey(sessionId)){
                session = sessionMap.get(sessionId);
            }
        }
        if(!sessionMap.containsKey(sessionId)){
            sessionMap.put(sessionId, session);
        }
        return session;
    }


    private static final Map<String, HttpSession> sessionMap = new HashMap<String, HttpSession>();

    public static HttpSession getSession(String sessionId){
        HttpSession session = sessionMap.get(sessionId);
        return session == null ? getSession() : session;
    }

    public static void removeSession(String sessionId){
        if(sessionMap.containsKey(sessionId)){
            sessionMap.remove(sessionId);
        }
    }
}
