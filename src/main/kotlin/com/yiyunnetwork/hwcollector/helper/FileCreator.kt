package com.yiyunnetwork.hwcollector.helper

import com.yiyunnetwork.hwcollector.data.FormData
import com.yiyunnetwork.hwcollector.repository.StuHomeworkRepository
import com.yiyunnetwork.hwcollector.repository.StudentRepository
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.util.*

@Component
class FileCreator : InitializingBean {

    private val logger = LoggerFactory.getLogger(this::class.java)

    @Autowired
    private var stuRepository: StudentRepository? = null

    @Autowired
    private var hwRepository: StuHomeworkRepository? = null

    @Autowired
    private var randomUtils: RandomUtils? = null

    @Throws(Exception::class)
    override fun afterPropertiesSet() {
        val dir = File(DIR_NAME)
        if (!dir.exists()) {
            val b = dir.mkdirs()
            if (!b) {
                throw IOException("创建目录失败：" + dir.absolutePath)
            }
            logger.info("创建目录：{}", dir.absolutePath)
        }
    }

    /**
     * 返回FormData的文件名
     * @param formData
     * @return
     */
    fun getUserDirName(formData: FormData): String {
        return String.format(
            formData.stu_class + File.separator + "%s-%s",
            formData.stu_no,
            formData.real_name
        )
    }

    /**
     * 创建文件
     * @param ins
     * @param formData
     * @throws IOException
     */
    @Throws(IOException::class)
    fun createUserDir(ins: InputStream, formData: FormData) {
        //检查学生所属班级的文件夹是否存在
        val dir = File(DIR_NAME + File.separator + formData.stu_class)
        if (!dir.exists()) {
            val b = dir.mkdirs()
            if (!b) {
                throw IOException("创建目录失败：" + dir.absolutePath)
            }
            logger.info("创建目录：{}", dir.absolutePath)
        }
        //检查学生文件夹是否存在
        val userDir = File(DIR_NAME + File.separator + getUserDirName(formData))
        if (!userDir.exists()) {
            val b = userDir.mkdirs()
            if (!b) {
                throw IOException("创建目录失败：" + userDir.absolutePath)
            }
            logger.info("创建目录：{}", userDir.absolutePath)
        }
        //将上传的文件写入到学生文件夹中，并重命名为学生姓名-学号-随机字符串.jpg
        val file = File(userDir, formData.real_name + "-" + formData.stu_no + "-" + randomUtils!!.getRandomString(8) + ".jpg")
        val b = file.createNewFile()
        if (!b) {
            throw IOException("创建文件失败：" + file.absolutePath)
        }
        logger.info("创建文件：{}", file.absolutePath)
        file.outputStream().use { os ->
            ins.copyTo(os)
        }
    }

    fun getFileList(className: String): List<FormData> {
        val root = File(DIR_NAME)
        val classNames = root.listFiles()?.filter { it.isDirectory }
        if (classNames.isNullOrEmpty()) return emptyList()
        val selectClass = runCatching { classNames.first { e: File? -> e!!.name == className } }.onFailure {
            throw RuntimeException("找不到目录")
        }.getOrNull()
        if (selectClass != null) {
            return selectClass.listFiles()?.map { e: File ->
                val formData = FormData()
                val names =
                    e.name.split("-".toRegex())
                        .dropLastWhile { it.isEmpty() }
                        .toTypedArray()
                formData.stu_class = className
                formData.stu_no = names[0]
                formData.real_name = names[1]
                val zips = e.listFiles() ?: return@map formData
                val file = runCatching { zips.first { it.isFile } }.getOrNull()
                    ?: return@map formData
                formData.fileName = file.name
                formData
            } ?: emptyList()
        } else return emptyList()
    }

    private fun delete(file: File): Boolean {
        if (file.isDirectory) {
            for (listFile in Objects.requireNonNull(file.listFiles())) {
                val delete = delete(listFile)
                if (!delete) {
                    throw RuntimeException("递归删除文件失败")
                }
            }
        }
        // 全部删除完了，删除目录本身
        return file.delete()
    }

    companion object {
        const val DIR_NAME = "collect-files"
    }
}
