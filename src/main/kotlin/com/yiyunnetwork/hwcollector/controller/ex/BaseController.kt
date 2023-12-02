package com.yiyunnetwork.hwcollector.controller.ex

import jakarta.servlet.http.HttpServletResponse
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.multipart.MultipartException
import java.io.IOException

@ControllerAdvice
class BaseController {
    @ResponseBody
    @ExceptionHandler(value = [MultipartException::class])
    @Throws(IOException::class)
    fun fileUploadExceptionHandler(response: HttpServletResponse, exception: MultipartException): String {
        response.sendRedirect("submit?error=" + exception.message)
        return "失败"
    }
}