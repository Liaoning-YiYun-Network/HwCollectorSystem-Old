package com.yiyunnetwork.hwcollector.repository

import com.yiyunnetwork.hwcollector.data.entity.HomeworkInfo
import org.springframework.data.repository.CrudRepository

interface HomeworkInfoRepository : CrudRepository<HomeworkInfo, Long>