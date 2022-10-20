package fit.wenchao.autobackup.config;

import fit.wenchao.autobackup.interceptor.HandlerMethodInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
public class InterceptorConfig implements WebMvcConfigurer {

    @Autowired
    ApplicationContext applicationContext;
    public <T> T getMyInterceptors(Class<T> clazz) {
        return applicationContext.getBean(clazz);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(getMyInterceptors(HandlerMethodInterceptor.class)).addPathPatterns("/**");
    }
}
