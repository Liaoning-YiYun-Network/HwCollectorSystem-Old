package com.yiyunnetwork.hwcollector.data.entity

import com.alibaba.excel.annotation.ExcelIgnore
import com.alibaba.excel.annotation.ExcelProperty
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import lombok.Data
import lombok.Getter
import lombok.Setter

@Getter
@Setter
@Data
@Entity(name = "student")
class Student {

    @Id
    @Column(
        name = "name",
        unique = true,
        nullable = false
    )
    @ExcelProperty("姓名")
    var real_name: String? = null

    @Column(name = "no")
    @ExcelProperty("学号")
    var stu_no: String? = null

    @Column(name = "class")
    @ExcelIgnore
    var stu_class: String? = null

    override fun toString(): String {
        return "Student(real_name=$real_name, stu_no=$stu_no, stu_class=$stu_class)"
    }
}