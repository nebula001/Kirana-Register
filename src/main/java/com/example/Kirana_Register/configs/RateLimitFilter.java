//package com.example.Kirana_Register.configs;
//
//import com.example.Kirana_Register.security.CustomUserDetails;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.http.HttpStatus;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.stereotype.Component;
//import org.springframework.web.servlet.HandlerInterceptor;
//
//import java.util.concurrent.TimeUnit;
//
//@Component
//public class RateLimitInterceptor implements HandlerInterceptor {
//    private static final int MAX_REQUESTS = 10; // Max requests per window
//    private static final long WINDOW_SECONDS = 60; // 1 hour window
//
//    @Autowired
//    private RedisTemplate<String, Object> redisTemplate;
//
//    @Override
//    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
//        String key = getRateLimitKey(request);
//        String redisKey = "rate_limit:" + key;
//
//        Long currentCount = redisTemplate.opsForValue().increment(redisKey);
//        if (currentCount == 1) {
//            redisTemplate.expire(redisKey, WINDOW_SECONDS, TimeUnit.SECONDS);
//        }
//
//        if (currentCount > MAX_REQUESTS) {
//            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
//            response.getWriter().write("Rate limit exceeded. Try again later.");
//            return false;
//        }
//
//        return true;
//    }
//
//    private String getRateLimitKey(HttpServletRequest request) {
//        Long userId = getUserIdFromSecurityContext();
//        if (userId != null) {
//            return "user:" + userId;
//        }
//        System.out.println("Not working");
//        return "ip:" + request.getRemoteAddr();
//    }
//
//    private Long getUserIdFromSecurityContext() {
//        try {
//            CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//            return userDetails.getUser().getId();
//        } catch (Exception e) {
//            return null;
//        }
//    }
//}


package com.example.Kirana_Register.configs;

import com.example.Kirana_Register.security.CustomUserDetails;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class RateLimitFilter extends OncePerRequestFilter {

    private static final int MAX_REQUESTS = 10;
    private static final long WINDOW_SECONDS = 60;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String key = getRateLimitKey(request);
        String redisKey = "rate_limit:" + key;

        Long currentCount = redisTemplate.opsForValue().increment(redisKey);
        if (currentCount == 1) {
            redisTemplate.expire(redisKey, WINDOW_SECONDS, TimeUnit.SECONDS);
        }

        if (currentCount > MAX_REQUESTS) {
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.getWriter().write("Rate limit exceeded. Try again later.");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private String getRateLimitKey(HttpServletRequest request) {
        Long userId = getUserIdFromSecurityContext();
        if (userId != null) {
            return "user:" + userId;
        }
        log.info("Checking the user id for rate limit key");
        return "ip:" + request.getRemoteAddr();
    }

    private Long getUserIdFromSecurityContext() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.isAuthenticated() && auth.getPrincipal() instanceof CustomUserDetails userDetails) {
                return userDetails.getUser().getId();
            }
        } catch (Exception ignored) {
        }
        return null;
    }
}
