package com.yiyunnetwork.hwcollector.listener

import com.google.gson.Gson
import com.yiyunnetwork.hwcollector.GlobalConstants
import com.yiyunnetwork.hwcollector.GlobalConstants.classes
import com.yiyunnetwork.hwcollector.applicationLogger
import com.yiyunnetwork.hwcollector.helper.StudentExcelHelper
import com.yiyunnetwork.hwcollector.repository.ClassFactory
import org.springframework.beans.factory.DisposableBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.stereotype.Component
import java.io.File

@Component
class StandardGuard : ApplicationRunner, DisposableBean {

    @Autowired
    private var classFactory: ClassFactory? = null

    @Autowired
    private var stuExcelHelper: StudentExcelHelper? = null

    override fun run(args: ApplicationArguments) {
        applicationLogger.info("欢迎使用熠云网络在线作业收集系统!")
        // 打印全部班级信息
        applicationLogger.info("当前班级信息如下:")
        classes.forEach {
            applicationLogger.info("班级名: ${it.className}")
        }
        // 将classes变量写入数据库
        classFactory!!.saveAll(classes)
        applicationLogger.info("已将classes信息写入数据库")
        // 读取每个班级的学生信息
        classes.forEach { classinfo ->
            val studentExcelFile = File("./stu-lists/${classinfo.className}.xlsx")
            if (studentExcelFile.exists()) {
                applicationLogger.info("正在读取${classinfo.className}的学生信息...")
                runCatching {
                    stuExcelHelper!!.readExcel(studentExcelFile)
                }.onFailure {
                    applicationLogger.error("读取${classinfo.className}的学生信息失败！", it)
                }.onSuccess {
                    applicationLogger.info("读取${classinfo.className}的学生信息成功！")
                }
            } else {
                applicationLogger.info("未找到${classinfo.className}的学生信息文件，将跳过该班级的学生信息读取")
            }
        }
    }

    override fun destroy() {
        applicationLogger.info("正在关闭在线作业收集系统...")

        /** 保存异常访问数据到本地 */
        val abnormalIPListFile = File("./data/abnormalIPList.json")
        applicationLogger.info("正在将abnormalIPMap中的内容写入abnormalIPMap.json文件")
        runCatching {
            abnormalIPListFile.writeText(Gson().toJson(GlobalConstants.abnormalIPMap))
        }.onFailure {
            applicationLogger.error("写入abnormalIPMap.json文件失败！", it)
        }.onSuccess {
            applicationLogger.info("写入abnormalIPMap.json文件成功！")
        }

        applicationLogger.info("在线作业收集系统已关闭!")
    }
}