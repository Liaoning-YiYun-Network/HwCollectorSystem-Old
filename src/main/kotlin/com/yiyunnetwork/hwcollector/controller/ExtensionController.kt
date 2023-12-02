package com.yiyunnetwork.hwcollector.controller

import com.google.gson.Gson
import com.yiyunnetwork.hwcollector.GlobalConstants.classes
import com.yiyunnetwork.hwcollector.data.bean.SimpleResponseBean
import com.yiyunnetwork.hwcollector.helper.ZipUtil
import com.yiyunnetwork.hwcollector.helper.getFileTree
import com.yiyunnetwork.hwcollector.repository.StuHomeworkRepository
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import java.io.File

@RestController
class ExtensionController(private val request: HttpServletRequest, private val response: HttpServletResponse) {

    @Autowired
    private var stuHwRepository: StuHomeworkRepository? = null

    @Autowired
    private var zipUtil: ZipUtil? = null

    private val logger = LoggerFactory.getLogger(this::class.java)

    /**
     * 删除数据库内的作业
     * 仅限管理员使用
     */
    @DeleteMapping("delete")
    fun delete(@RequestPart(name = "cleanAll") flag: String?, @RequestPart(name = "stuName") stuName: String?,
               @RequestPart(name = "stuNo") stuNo: String?, @RequestPart(name = "token") token: String?
    ): String {
        // 判断token是否正确
        if (token != "ciyeweiyang1314") {
            return Gson().toJson(SimpleResponseBean(403, "鉴权失败"))
        }
        // 判断是否清空数据库
        if (flag == "1") {
            // 清空数据库
            stuHwRepository!!.deleteAll()
            // 删除所有班级文件夹
            classes.forEach { clazz ->
                val dir = File("./collect-files/${clazz.className}")
                if (dir.exists()) {
                    dir.deleteRecursively()
                }
            }
            return Gson().toJson(SimpleResponseBean(200, "清空数据库成功"))
        } else {
            // 删除指定学生的作业
            if (stuName == null) {
                return Gson().toJson(SimpleResponseBean(400, "参数错误"))
            }
            stuHwRepository!!.deleteById(stuName)
            // 删除指定学生的文件夹
            classes.forEach { clazz ->
                val dir = File("./collect-files/${clazz.className}/$stuNo-$stuName")
                if (dir.exists()) {
                    dir.deleteRecursively()
                }
            }
            return Gson().toJson(SimpleResponseBean(200, "删除指定学生的作业成功"))
        }
    }

    /**
     * 下载指定班级的作业
     */
    @GetMapping("download")
    fun download(@RequestParam(name = "className") className: String?): String? {
        // 判断参数是否正确
        if (className == null) {
            return Gson().toJson(SimpleResponseBean(400, "参数错误"))
        }
        // 判断是否存在该班级
        if (!classes.map { it.className }.contains(className)) {
            return Gson().toJson(SimpleResponseBean(400, "参数错误"))
        }
        // 设置响应超时时间为三分钟，避免打包时间过长导致前端超时
        response.setHeader("Connection", "Keep-Alive")
        response.setHeader("Keep-Alive", "timeout=180")
        // 打包班级作业
        logger.info("开始打包班级：$className 的作业")
        // 将该班级所在的文件夹打包，放到tmp目录下，重命名为班级名-时间戳.zip
        val zipName = "download-${System.currentTimeMillis()}.zip"

        val zipPath = runCatching {
            zipUtil!!.zipFile("./collect-files/$className", "./tmp", zipName)
        }.onFailure {
            logger.error("打包班级：$className 的作业失败，原因：${it.message}")
        }.getOrNull() ?: return Gson().toJson(SimpleResponseBean(500, "打包失败，请联系管理员"))
        // 将打包好的文件返回给前端
        // 设置content-type为zip
        response.contentType = "application/zip"
        response.setHeader("Content-Disposition", "attachment;filename=${zipName}")
        response.outputStream.use {
            it.write(File(zipPath).readBytes())
        }
        logger.info("打包班级：$className 的作业完成")
        return null
    }

    /**
     * 获取指定班级的所在文件夹的文件树
     *
     * @param className 班级名
     * @return 文件树
     */
    @GetMapping("file-tree")
    fun fileTree(@RequestParam(name = "className") className: String?): String {
        // 判断参数是否正确
        if (className == null) {
            return Gson().toJson(SimpleResponseBean(400, "参数错误"))
        }
        // 判断是否存在该班级
        if (!classes.map { it.className }.contains(className)) {
            return Gson().toJson(SimpleResponseBean(400, "参数错误"))
        }
        // 获取该班级所在文件夹的文件树
        val fileTree = runCatching {
            File("./collect-files/$className").getFileTree()
        }.onFailure {
            logger.error("获取班级：$className 的文件树失败，原因：${it.message}")
        }.getOrNull() ?: return Gson().toJson(SimpleResponseBean(500, "获取文件树失败！"))
        return Gson().toJson(SimpleResponseBean(200, "获取文件树成功。\n$fileTree"))
    }
}