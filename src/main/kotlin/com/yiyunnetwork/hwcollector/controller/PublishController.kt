package com.yiyunnetwork.hwcollector.controller

import com.yiyunnetwork.hwcollector.GlobalConstants
import com.yiyunnetwork.hwcollector.data.entity.HomeworkInfo
import com.yiyunnetwork.hwcollector.repository.HomeworkInfoRepository
import com.yiyunnetwork.hwcollector.repository.StuHomeworkRepository
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.ui.Model
import org.springframework.util.StringUtils
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartHttpServletRequest
import org.springframework.web.servlet.ModelAndView
import java.io.File
import java.text.DateFormat
import java.text.SimpleDateFormat

@RestController
class PublishController {

    @Autowired
    private var hwInfoRepository: HomeworkInfoRepository? = null

    @Autowired
    private var stuHwRepository: StuHomeworkRepository? = null

    @GetMapping("", "publish")
    fun main(@RequestParam(name = "error") error: String?, model: Model): ModelAndView {
        if (StringUtils.hasText(error)) {
            model.apply {
                addAttribute("hasError", true)
                addAttribute("errorInfo", error)
            }
        } else {
            model.apply {
                addAttribute("hasError", false)
                addAttribute("errorInfo", error)
            }
        }
        return ModelAndView("publish")
    }

    @PostMapping("hwpub")
    fun publish(request: HttpServletRequest,
                response: HttpServletResponse,
                model: Model,
                hwInfo: HomeworkInfo
    ): ModelAndView {
        val multipartHttpServletRequest = request as MultipartHttpServletRequest
        // 获取表单中auth_token的值
        val authToken = multipartHttpServletRequest.getParameter("auth_token")
        // 判断auth_token是否正确
        if (authToken != "HMZKKD89gyiu*Kkjhyuky*&hjbvjIYUH") {
            model.apply {
                addAttribute("hasError", true)
                addAttribute("errorInfo", "auth_token错误")
            }
            return ModelAndView("publish")
        }
        // 检查hwInfo中是否有空值
        if (hwInfo.hwContent.isNullOrBlank() || hwInfo.hwDdlDate.isNullOrBlank() || hwInfo.hwTitle.isNullOrBlank()) {
            model.apply {
                addAttribute("hasError", true)
                addAttribute("errorInfo", "请填写完整信息")
            }
            return ModelAndView("publish")
        }
        // 检查ddl是否可以转换为日期
        // 规范格式为yyyy-MM-dd HH:mm:ss
        // 例如2021-09-01 23:59:59，2021-09-01 23:59:00，尝试转换为Date
        val dateFormat = Regex("""\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2}""")
        if (!hwInfo.hwDdlDate!!.matches(dateFormat)) {
            model.apply {
                addAttribute("hasError", true)
                addAttribute("errorInfo", "日期格式不正确")
            }
            return ModelAndView("publish")
        }
        // 检查ddl是否已经过期
        val ddlDate = dateFormat.find(hwInfo.hwDdlDate!!)!!.value
        val ddl = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(ddlDate)
        if (ddl.before(DateFormat.getDateTimeInstance().parse(DateFormat.getDateTimeInstance().format(System.currentTimeMillis())))) {
            model.apply {
                addAttribute("hasError", true)
                addAttribute("errorInfo", "截止日期已过期")
            }
            return ModelAndView("publish")
        }
        hwInfoRepository!!.save(hwInfo)
        // 清空数据库
        stuHwRepository!!.deleteAll()
        // 删除所有班级文件夹
        GlobalConstants.classes.forEach { clazz ->
            val dir = File("./collect-files/${clazz.className}")
            if (dir.exists()) {
                dir.deleteRecursively()
            }
        }
        return ModelAndView("pub_success")
    }

}