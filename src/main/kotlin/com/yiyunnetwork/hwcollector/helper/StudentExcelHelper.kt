package com.yiyunnetwork.hwcollector.helper

import com.alibaba.excel.EasyExcel
import com.yiyunnetwork.hwcollector.data.entity.Student
import com.yiyunnetwork.hwcollector.data.listener.StudentDataListener
import com.yiyunnetwork.hwcollector.repository.StudentRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.io.File
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

@Component
class StudentExcelHelper {

    private val executor = ThreadPoolExecutor(
        1,
        5,
        30, TimeUnit.SECONDS,
        ArrayBlockingQueue(30),
        ThreadPoolExecutor.AbortPolicy()
    )

    @Autowired
    private var stuRepository: StudentRepository? = null

    /**
     * 初始化文件
     * @param excel
     */
    @Synchronized
    private fun createFile(excel: File) {
        EasyExcel.write(excel, Student::class.java)
            .sheet()
            .doWrite(emptyList<Student>())
    }

    /**
     * 初始化文件
     * @param excel
     */
    @Synchronized
    private fun createFile(excel: String) {
        EasyExcel.write(excel, Student::class.java)
            .sheet()
            .doWrite(emptyList<Student>())
    }

    @Synchronized
    fun saveToExcel(excel: File, formData: Student?) {
        if (!excel.exists()) {
            createFile(excel)
        }
        executor.execute {
            EasyExcel.write(excel, Student::class.java)
                .sheet()
                .doWrite(listOf(formData))
        }
    }

    @Synchronized
    fun saveToExcel(excel: String, formData: Student?) {
        val file = File(excel)
        if (!file.exists()) {
            createFile(file)
        }
        executor.execute {
            EasyExcel.write(file, Student::class.java)
                .sheet()
                .doWrite(listOf(formData))
        }
    }

    @Synchronized
    fun readExcel(excel: String) {
        val file = File("./stu-lists" + File.separator + excel)
        val className = file.nameWithoutExtension
        // 这里 需要指定读用哪个class去读，然后读取第一个sheet 文件流会自动关闭
        EasyExcel.read(file, Student::class.java, StudentDataListener(className, stuRepository!!)).sheet().doRead()
    }

    @Synchronized
    fun readExcel(excel: File) {
        val className = excel.nameWithoutExtension
        // 这里 需要指定读用哪个class去读，然后读取第一个sheet 文件流会自动关闭
        EasyExcel.read(excel, Student::class.java, StudentDataListener(className, stuRepository!!)).sheet().doRead()
    }

}