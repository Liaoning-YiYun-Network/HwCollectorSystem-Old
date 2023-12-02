package com.yiyunnetwork.hwcollector

import com.google.gson.Gson
import com.yiyunnetwork.hwcollector.GlobalConstants.abnormalIPMap
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@SpringBootApplication
class HwCollectorApplication

val applicationLogger by lazy { LoggerFactory.getLogger(HwCollectorApplication::class.java) }

fun main(args: Array<String>) {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd-HH:mm:ss")
    val startTime = dateFormat.format(Date())
    System.setProperty("START_TIME", startTime)
    // 设置日志文件路径
    System.setProperty("LOG_PATH", "./logs/log-${startTime.replace(":", "-")}.log")
    // 检查运行目录下是否存在data目录，不存在则创建
    val dataDir = File("./data")
    if (!dataDir.exists()) {
        dataDir.mkdir()
    }
    // 检查运行目录下是否存在abnormalIPList.json文件，不存在则创建
    val abnormalIPListFile = File("./data/abnormalIPList.json")
    abnormalIPMap = if (!abnormalIPListFile.exists()) {
        abnormalIPListFile.createNewFile()
        // 初始化GlobalConstants中的abnormalIPMap
        hashMapOf()
    } else {
        val tmp = Gson().fromJson(abnormalIPListFile.readText(), HashMap::class.java)
        if (tmp != null) {
            tmp as HashMap<String, Int>
        } else {
            hashMapOf()
        }
    }
    // 创建一个线程，每五分钟将abnormalIPMap中的内容通过Gson写入abnormalIPMap.json文件
    Thread {
        while (true) {
            Thread.sleep(300000)
            applicationLogger.info("正在执行定时任务，将abnormalIPMap中的内容写入abnormalIPMap.json文件")
            runCatching {
                abnormalIPListFile.writeText(Gson().toJson(abnormalIPMap))
            }.onFailure {
                applicationLogger.error("写入abnormalIPMap.json文件失败！", it)
            }.onSuccess {
                applicationLogger.info("写入abnormalIPMap.json文件成功！")
            }
        }
    }.start()
    runApplication<HwCollectorApplication>(*args)
}
