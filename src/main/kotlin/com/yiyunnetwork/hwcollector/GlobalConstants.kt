package com.yiyunnetwork.hwcollector

import com.yiyunnetwork.hwcollector.data.entity.ClassInfo
import com.yiyunnetwork.hwcollector.data.entity.Student
import java.io.File

object GlobalConstants {
    lateinit var abnormalIPMap: HashMap<String, Int>

    val classes: List<ClassInfo> by lazy {
        val classesDir = File("./stu-lists")
        if (!classesDir.exists()) {
            classesDir.mkdir()
        }
        // 获取classes目录下的所有文件的文件名，即班级名，需要去除后缀
        val classNames = classesDir.listFiles()?.map { it.nameWithoutExtension } ?: listOf()
        classNames.map { ClassInfo().apply { className = it } }
    }

    lateinit var stuLists: Map<String, List<Student>>
}