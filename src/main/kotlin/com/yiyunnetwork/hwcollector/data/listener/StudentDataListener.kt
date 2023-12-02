package com.yiyunnetwork.hwcollector.data.listener

import com.alibaba.excel.context.AnalysisContext
import com.alibaba.excel.read.listener.ReadListener
import com.alibaba.excel.util.ListUtils
import com.yiyunnetwork.hwcollector.data.entity.Student
import com.yiyunnetwork.hwcollector.repository.StudentRepository
import org.slf4j.LoggerFactory

/**
 * 数据读取监听器
 */
class StudentDataListener(private val className: String,
                          private val stuRepository: StudentRepository) : ReadListener<Student?> {

    private val logger = LoggerFactory.getLogger(this::class.java)

    /**
     * 缓存的数据
     */
    private val cachedDataList = ListUtils.newArrayListWithExpectedSize<Student>(BATCH_COUNT)
    override fun invoke(data: Student?, context: AnalysisContext) {
        cachedDataList.add(data)
        logger.info("读取到数据：{},添加到缓存", data)
        if (cachedDataList.size >= BATCH_COUNT) {
            logger.info("缓存已满，开始写入数据库")
            stuRepository.saveAll(cachedDataList.map {
                it.stu_class = className
                it
            })
            cachedDataList.clear()
        }
    }
    override fun doAfterAllAnalysed(context: AnalysisContext) {
        logger.info("读取已完成！")
        stuRepository.saveAll(cachedDataList.map {
            it.stu_class = className
            it
        })
        cachedDataList.clear()
        logger.info("数据入库完成！")
    }

    companion object {
        /**
         * 缓存List的最大容量
         */
        private const val BATCH_COUNT = 100
    }
}
