package com.yiyunnetwork.hwcollector.data.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id

@Entity(name = "stu_homework")
class StuHomework {

    @Id
    @Column(
        name = "name",
        unique = true,
        nullable = false
    )
    var stuName: String? = null

    @Column(name = "homework_file")
    var hwFilePath: String? = null

}