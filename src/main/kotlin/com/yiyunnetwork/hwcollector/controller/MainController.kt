package com.yiyunnetwork.hwcollector.controller

import com.yiyunnetwork.hwcollector.GlobalConstants.classes
import com.yiyunnetwork.hwcollector.data.FormData
import com.yiyunnetwork.hwcollector.data.entity.HomeworkInfo
import com.yiyunnetwork.hwcollector.helper.FileCreator
import com.yiyunnetwork.hwcollector.helper.UpdateLogGetter
import com.yiyunnetwork.hwcollector.helper.getWhenNull
import com.yiyunnetwork.hwcollector.repository.HomeworkInfoRepository
import com.yiyunnetwork.hwcollector.repository.StuHomeworkRepository
import com.yiyunnetwork.hwcollector.repository.StudentRepository
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.ui.Model
import org.springframework.util.StringUtils
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.ModelAndView

@RestController
class MainController(private val request: HttpServletRequest, private val response: HttpServletResponse) {

    @Autowired
    private val fileCreator: FileCreator? = null

    private val logger = LoggerFactory.getLogger(this::class.java)

    @Autowired
    private var hwInfoRepository: HomeworkInfoRepository? = null

    @Autowired
    private var stuHwRepository: StuHomeworkRepository? = null

    @Autowired
    private var stuRepository: StudentRepository? = null

    @GetMapping("", "submit")
    fun main(@RequestParam(name = "error") error: String?, @RequestParam(name = "classname") classname: String?, model: Model): ModelAndView? {
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
        val classNames = classes.map { it.className }
        if (classNames.isNotEmpty()) {
            val select = if (classNames.contains(classname)) classname else classNames[0]
            // 获取班级学生列表
            val students = stuRepository!!.findAll().filter { it.stu_class == select }
            // 获取作业列表中属于该班级的学生的作业信息
            val stuHwList = stuHwRepository!!.findAll().filter { stuHomework ->
                stuHomework.stuName in students.map { it.real_name }
            }
            // 转换到FormData
            val stuHwFormDataList = students.map {
                FormData().apply {
                    stu_class = it.stu_class
                    stu_no = it.stu_no
                    real_name = it.real_name
                    fileName = runCatching {
                        stuHwList.find { stuHw -> stuHw.stuName == it.real_name }?.hwFilePath
                    }.getWhenNull("")
                }
            }
            // 获取最新一份作业信息
            val latestHwInfo = runCatching {
                hwInfoRepository!!.findAll().last()
            }.onFailure {
                logger.error("获取最新作业失败，作业可能不存在，请稍后再试！")
            }.getOrDefault(HomeworkInfo().apply {
                hwDdlDate = "1970-01-01 00:00:00"
                hwTitle = "作业不存在"
                hwContent = "获取最新作业失败，作业可能不存在，请稍后再试！"
            })
            UpdateLogGetter.getUpdateLog().let {
                model.addAttribute("updateLog", it)
            }
            model.apply {
                addAttribute("hwDdlDate", latestHwInfo.hwDdlDate)
                addAttribute("hwTitle", latestHwInfo.hwTitle)
                addAttribute("hwContent", latestHwInfo.hwContent)
                addAttribute("classnames", classNames)
                addAttribute("classname", select)
                addAttribute("students", stuHwFormDataList)
            }
        }
        return ModelAndView("submit")
    }

}
