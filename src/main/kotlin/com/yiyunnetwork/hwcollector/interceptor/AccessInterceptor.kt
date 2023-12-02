package com.yiyunnetwork.hwcollector.interceptor

import com.yiyunnetwork.hwcollector.GlobalConstants.abnormalIPMap
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.web.servlet.HandlerInterceptor

class AccessInterceptor : HandlerInterceptor {

    private val logger = LoggerFactory.getLogger(this::class.java)

    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        val ip = request.remoteAddr
        // 打印请求信息
        logger.info("IP: $ip 请求了 ${request.requestURI}，请求方式为 ${request.method}")
        // 判断请求是否为重定向，如果是则跳过
        if (request.requestURI == "/forbidden" || request.requestURI == "/remove") {
            return super.preHandle(request, response, handler)
        }
        // 判断被记录的违规请求IP是否超过5次，如果超过则跳转到403页面
        if (ip in abnormalIPMap && abnormalIPMap[ip]!! >= 5) {
            logger.error("IP: $ip 的请求达到封禁上限，已禁止访问")
            // 跳转到forbidden页面
            response.sendRedirect("forbidden")
            return false
        }
        return super.preHandle(request, response, handler)
    }
}