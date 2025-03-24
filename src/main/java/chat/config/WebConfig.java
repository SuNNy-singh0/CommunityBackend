package chat.config;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import chat.service.DailyLoginInterceptor;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private DailyLoginInterceptor dailyLoginInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(dailyLoginInterceptor)
                .addPathPatterns("/**") // Apply to all endpoints
                .excludePathPatterns("/auth/login", "/auth/register"); // Exclude login & register
    }
}
