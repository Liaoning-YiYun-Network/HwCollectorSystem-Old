package com.yiyunnetwork.hwcollector.repository

import com.yiyunnetwork.hwcollector.data.entity.Student
import org.springframework.data.repository.CrudRepository

interface StudentRepository : CrudRepository<Student, String>