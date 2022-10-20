package fit.wenchao.autobackup.interceptor;

import fit.wenchao.autobackup.utils.ThreadLocalUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class HandlerMethodInterceptor implements HandlerInterceptor {
    public static final String HANDLER_METHOD_KEY = "handler-method-key";

    public static final String HANDLER_METHOD_NAME = "handler-method-name";
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (handler instanceof HandlerMethod) {
            ThreadLocalUtils.putThreadInfo(HANDLER_METHOD_NAME, ((HandlerMethod) handler).getMethod().getName());
            request.setAttribute(HANDLER_METHOD_KEY, handler);
        }
        return true;
    }
}
