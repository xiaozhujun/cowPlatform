package org.whut.platform.business.user.security;

/**
 * Created with IntelliJ IDEA.
 * User: xiaozhujun
 * Date: 14-12-28
 * Time: 下午2:17
 * To change this template use File | Settings | File Templates.
 */
import org.whut.platform.fundamental.exception.BusinessException;
import org.whut.platform.fundamental.logger.PlatformLogger;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CaptchaFilter implements Filter{

    private static final PlatformLogger logger = PlatformLogger.getLogger(CaptchaFilter.class);

    @Override
    public void destroy() {
        // TODO Auto-generated method stub

    }

    @Override
    public void doFilter(ServletRequest arg0, ServletResponse arg1,
                         FilterChain arg2) throws IOException, ServletException {
        // TODO Auto-generated method stub
        HttpServletRequest request = (HttpServletRequest) arg0;
        HttpServletResponse response = (HttpServletResponse) arg1;

        String yanzhengm = request.getParameter("j_captcha");
        String sessionyanz = (String)request.getSession(true).getAttribute("yzkeyword");
        if(yanzhengm.equals(sessionyanz)){
            arg2.doFilter(request, response);
        }else{
            response.setHeader("Content-type", "application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":501,\"msg\":\"验证码错误\"}");
            logger.info("{\"code\":501,\"msg\":\"验证码错误\"}");

            response.getWriter().flush();
            response.getWriter().close();
            return;
        }
    }

    @Override
    public void init(FilterConfig arg0) throws ServletException {
        // TODO Auto-generated method stub

    }


}