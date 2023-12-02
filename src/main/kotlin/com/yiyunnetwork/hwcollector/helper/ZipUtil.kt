package com.yiyunnetwork.hwcollector.helper

import org.springframework.stereotype.Component
import java.io.BufferedInputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

@Component
class ZipUtil {

    /**
     * 压缩文件
     *
     * @param srcPathName 源文件路径
     * @param zipFilePath 压缩后文件路径
     * @param zipFileName 压缩后文件名
     * @return 压缩后文件路径
     */
    fun zipFile(srcPathName: String, zipFilePath: String, zipFileName: String): String {
        val srcDir = File(srcPathName)
        if (!srcDir.exists()) {
            throw RuntimeException(srcPathName + "不存在！")
        }
        val zipFile = File("$zipFilePath/$zipFileName")
        if (zipFile.exists()) {
            throw RuntimeException(zipFilePath + "目录下存在名字为" + zipFileName + "打包文件")
        } else {
            // 判断父目录是否存在
            if (!zipFile.parentFile.exists()) {
                // 不存在就创建一个
                zipFile.parentFile.mkdirs()
            }
            // 创建文件
            zipFile.createNewFile()
        }
        val zipOut = ZipOutputStream(FileOutputStream(zipFile))
        zipOut.use {
            zip(srcDir, zipOut, "")
        }
        return "$zipFilePath/$zipFileName"
    }

    /**
     * 递归压缩文件
     */
    private fun zip(file: File, zipOut: ZipOutputStream, rootPath: String) {
        if (file.isFile) {
            val buffer = ByteArray(1024 * 4)
            val input = BufferedInputStream(FileInputStream(file), 1024 * 4)
            zipOut.putNextEntry(ZipEntry(rootPath + file.name))
            var len = -1
            while (input.read(buffer, 0, 1024 * 4).also { len = it } != -1) {
                zipOut.write(buffer, 0, len)
            }
            input.close()
            zipOut.flush()
            zipOut.closeEntry()
        } else {
            val fileList = file.listFiles()
            if (fileList!!.isEmpty()) {
                zipOut.putNextEntry(ZipEntry(rootPath + file.name + File.separator))
                zipOut.closeEntry()
            } else {
                for (f in fileList) {
                    zip(f, zipOut, rootPath + file.name + File.separator)
                }
            }
        }
    }
}