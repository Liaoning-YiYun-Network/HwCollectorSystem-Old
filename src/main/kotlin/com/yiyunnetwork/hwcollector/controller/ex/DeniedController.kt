package com.yiyunnetwork.hwcollector.controller.ex

import com.yiyunnetwork.hwcollector.GlobalConstants.abnormalIPMap
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.ModelAndView

@RestController
class DeniedController(private val request: HttpServletRequest, private val response: HttpServletResponse) {

    private val logger = LoggerFactory.getLogger(this::class.java)

    @GetMapping("denied")
    fun denied(): ModelAndView {
        // 将当前请求的IP对应键值加一
        val ip = request.remoteAddr
        val count = abnormalIPMap[ip] ?: 0
        abnormalIPMap[ip] = count + 1
        // 记录日志
        logger.error("IP: $ip 触发了违规请求，已记录")
        // 向前端返回403
        response.status = 403
        return ModelAndView("denied")
    }

    @GetMapping("forbidden")
    fun forbidden(model: Model): ModelAndView {
        // 向前端返回403
        response.status = 403
        logger.error("IP: ${request.remoteAddr} 的请求达到封禁上限，已禁止访问")
        model.addAttribute("ipAddr", request.remoteAddr)
        return ModelAndView("forbidden")
    }

    @DeleteMapping("remove")
    fun remove(@RequestPart(name = "token") token: String?, @RequestPart(name = "ip") ip: String, model: Model): ModelAndView? {
        // 判断token是否正确
        if (token != "ciyeweiyang1314") {
            // 向前端返回403
            response.status = 403
            // 直接封禁访问来源IP
            abnormalIPMap[ip] = 5
            logger.error("IP: ${request.remoteAddr} 进行了高级权限接口访问，鉴权失败，IP已封禁")
            response.sendRedirect("forbidden")
            return null
        }
        // 将当前请求的IP对应键值置零
        abnormalIPMap[ip] = 0
        // 记录日志
        logger.info("IP: $ip 的封禁已解除")
        model.addAttribute("ipAddr", ip)
        return ModelAndView("remove")
    }

}