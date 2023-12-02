package com.yiyunnetwork.hwcollector.helper

object UpdateLogGetter {

    /**
     * 获取更新日志，文件位于resources/update-log.txt
     * 将每行的内容分别保存到ArrayList中
     * @return 更新日志
     */
    fun getUpdateLog(): ArrayList<String> {
        val updateLog = ArrayList<String>()
        val inputStream = this::class.java.classLoader.getResourceAsStream("update-log.txt")
        inputStream!!.bufferedReader().useLines { lines ->
            lines.forEach {
                updateLog.add(it)
            }
        }
        return updateLog
    }
}