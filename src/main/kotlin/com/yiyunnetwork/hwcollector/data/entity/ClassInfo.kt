package com.yiyunnetwork.hwcollector.data.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id

@Entity(name = "class")
class ClassInfo {

    @Id
    @Column(
        name = "name",
        unique = true,
        nullable = false
    )
    var className: String? = null

}