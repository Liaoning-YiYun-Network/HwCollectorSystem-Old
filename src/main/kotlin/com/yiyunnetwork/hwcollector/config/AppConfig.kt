package com.yiyunnetwork.hwcollector.config

import com.yiyunnetwork.hwcollector.interceptor.AccessInterceptor
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class AppConfig : WebMvcConfigurer {
    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(AccessInterceptor()).addPathPatterns("/**")
    }
}