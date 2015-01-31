package org.whut.platform.business.user.security;

import org.whut.platform.fundamental.exception.BusinessException;
import org.whut.platform.fundamental.logger.PlatformLogger;
import org.whut.platform.fundamental.util.json.JsonMapper;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: xiaozhujun
 * Date: 15-1-29
 * Time: 下午10:56
 * To change this template use File | Settings | File Templates.
 */
public class UserInfoFiler implements Filter {

    public static final PlatformLogger logger = PlatformLogger.getLogger(UserInfoFiler.class);

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        String currentURL = request.getRequestURI(); // 取得根目录所对应的绝对路径:
        String targetURL = currentURL.substring(currentURL.indexOf("/", 1),
                currentURL.length()); // 截取到当前文件名用于比较
        logger.info("targetUrl: "+targetURL);
        if ("/rs/user/isLogin".equals(targetURL)) {
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
            return;
        }
        filterChain.doFilter(request,response);
       }

    @Override
    public void destroy() {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
