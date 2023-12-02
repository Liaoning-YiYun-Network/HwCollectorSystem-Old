package com.yiyunnetwork.hwcollector.data.entity

import jakarta.persistence.*

@Entity(name = "hw_info")
class HomeworkInfo {

    @Id
    @Column(
        name = "id",
        unique = true,
        nullable = true
    )
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var hwId: Long? = null

    @Column(name = "class")
    var hwClass: String? = null

    @Column(name = "title")
    var hwTitle: String? = null

    @Column(name = "content")
    var hwContent: String? = null

    @Column(name = "ddl_date")
    var hwDdlDate: String? = null
}