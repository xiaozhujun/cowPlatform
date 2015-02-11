package org.whut.platform.business.user.security;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.whut.platform.fundamental.exception.BusinessException;
import org.whut.platform.fundamental.logger.PlatformLogger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: xiaozhujun
 * Date: 15-1-31
 * Time: 下午10:55
 * To change this template use File | Settings | File Templates.
 */
public class MyAuthenticationFailureHandler implements AuthenticationFailureHandler {

    private static final PlatformLogger logger = PlatformLogger.getLogger(MyAuthenticationFailureHandler.class);

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        response.setContentType("text/html; charset=utf-8");
        response.setHeader("pragma", "no-cache");
        response.setHeader("cache-control", "no-cache");
        response.getWriter().write("{\"code\":500,\"msg\":\"账号或者密码不正确！\"}");
        logger.info("{\"code\":500,\"msg\":\"账号或者密码不正确！\"}");
        response.getWriter().flush();
        response.getWriter().close();
    }
}
