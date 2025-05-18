package com.ureca.juksoon.global.response;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.util.WebUtils;

public class CookieUtils {
    public static void setResponseBasicCookie(String key, String value, int expiredMs, HttpServletResponse response){
        String cookieValue = String.format(
        "%s=%s; Path=/; Max-Age=%d; HttpOnly; SameSite=None; Secure",
        key, value, expiredMs
    );
    response.addHeader("Set-Cookie", cookieValue);
        // Cookie cookie = new Cookie(key, value);
        // cookie.setPath("/");
        // cookie.setHttpOnly(true);
        // cookie.setMaxAge(expiredMs);
        // response.addCookie(cookie);
    }

    public static void deleteCookie(String key, String value, HttpServletResponse response){
        Cookie cookie = new Cookie(key, value);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }

    public static Cookie getCookie(String key, HttpServletRequest request){
        return WebUtils.getCookie(request, key);
    }
}
