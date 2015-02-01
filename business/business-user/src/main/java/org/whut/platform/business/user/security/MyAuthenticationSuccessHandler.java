package org.whut.platform.business.user.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
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
 * Time: 下午10:48
 * To change this template use File | Settings | File Templates.
 */
public class MyAuthenticationSuccessHandler implements AuthenticationSuccessHandler{

    private static final PlatformLogger logger = PlatformLogger.getLogger(MyAuthenticationSuccessHandler.class);

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        response.setHeader("Content-type","application/json;charset=UTF-8");
        MyUserDetail userDetail = null;
        try{
            userDetail = UserContext.currentUser();
        }catch (BusinessException be){
            userDetail=null;
        }

        if(userDetail==null){
            response.getWriter().write("{\"code\":500,\"msg\":\"false\"}");
            logger.info("{\"code\":500,\"msg\":\"false\"}");

        }else {
            response.getWriter().write("{\"code\":200,\"msg\":{\"username\":\""+userDetail.getUserName()+"\"}}");
            logger.info("{\"code\":200,\"msg\":{\"username\":\""+userDetail.getUserName()+"\"}}");
        }
        response.getWriter().flush();
        response.getWriter().close();
    }
}
