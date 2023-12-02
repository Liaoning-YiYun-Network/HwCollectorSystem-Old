package com.yiyunnetwork.hwcollector.repository

import org.springframework.data.repository.CrudRepository
import com.yiyunnetwork.hwcollector.data.entity.ClassInfo

interface ClassFactory : CrudRepository<ClassInfo, String>