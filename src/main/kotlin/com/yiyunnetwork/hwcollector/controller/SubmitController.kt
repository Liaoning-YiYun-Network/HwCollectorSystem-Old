package com.yiyunnetwork.hwcollector.controller

import com.yiyunnetwork.hwcollector.data.FormData
import com.yiyunnetwork.hwcollector.data.entity.StuHomework
import com.yiyunnetwork.hwcollector.helper.FileCreator
import com.yiyunnetwork.hwcollector.repository.HomeworkInfoRepository
import com.yiyunnetwork.hwcollector.repository.StuHomeworkRepository
import com.yiyunnetwork.hwcollector.repository.StudentRepository
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull
import org.springframework.ui.Model
import org.springframework.util.StringUtils
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.servlet.ModelAndView
import java.io.IOException
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.*

@RestController
class SubmitController {

    private val logger = LoggerFactory.getLogger(this::class.java)

    @Autowired
    private var fileCreator: FileCreator? = null

    @Autowired
    private var hwRepository: StuHomeworkRepository? = null

    @Autowired
    private var stuRepository: StudentRepository? = null

    @Autowired
    private var hwInfoRepository: HomeworkInfoRepository? = null

    @PostMapping("", "form")
    @Throws(IOException::class)
    fun main(
        request: HttpServletRequest,
        response: HttpServletResponse,
        model: Model,
        formData: FormData,
        @RequestParam(name = "file") photos: List<MultipartFile>?
    ): ModelAndView? {
        try {
            // val multipartHttpServletRequest = request as MultipartHttpServletRequest
            val files = photos ?: throw RuntimeException("未找到上传的文件")
            checkForm(formData)
            // 判断是否已经提交过作业
            val stuHw = hwRepository!!.findByIdOrNull(formData.real_name!!)
            if (stuHw != null) {
                throw RuntimeException("已经提交过作业")
            }
            files.forEach { file ->
                if (file.isEmpty) {
                    throw RuntimeException("文件为空")
                }
                if (file.originalFilename == null) {
                    throw RuntimeException("文件名为空")
                }
                val ins = file.inputStream
                fileCreator!!.createUserDir(ins, formData)
            }
            // 保存作业信息到数据库
            hwRepository!!.save(
                StuHomework().apply {
                    stuName = formData.real_name
                    hwFilePath = fileCreator!!.getUserDirName(formData)
                }
            )
        } catch (e: Exception) {
            logger.error("提交失败", e)
            model.addAttribute("hasError", true)
            model.addAttribute("errorInfo", e.message)
            response.sendRedirect("submit?error=" + URLEncoder.encode(e.message, StandardCharsets.UTF_8))
            return null
        }
        return ModelAndView("success")
    }

    private fun checkForm(formData: FormData) {
        // 从数据库获取最新一份作业信息
        val latestHwInfo = hwInfoRepository!!.findAll().last()
        // 检查时间
        val ddl = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(latestHwInfo.hwDdlDate)
        if (ddl.before(Date.from(Instant.now()))) {
            throw RuntimeException("提交时间已经过了")
        }
        if (!StringUtils.hasText(formData.real_name) ||
            !StringUtils.hasText(formData.stu_class) ||
            !StringUtils.hasText(formData.stu_no)
        ) {
            throw RuntimeException("参数不正确")
        }
        // 从数据库检查学生信息是否有效
        val student = stuRepository!!.findById(formData.real_name!!).orElse(null) ?: throw RuntimeException("学生信息不存在")
        if (student.stu_no != formData.stu_no) throw RuntimeException("学生信息不正确")
    }
}