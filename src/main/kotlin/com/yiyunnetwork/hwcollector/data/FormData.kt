package com.yiyunnetwork.hwcollector.data

import com.alibaba.excel.annotation.ExcelIgnore
import com.alibaba.excel.annotation.ExcelProperty
import lombok.Data
import lombok.Getter
import lombok.Setter

@Getter
@Setter
@Data
class FormData {

    @ExcelIgnore
    var stu_class: String? = null

    @ExcelProperty("学号")
    var stu_no: String? = null

    @ExcelProperty("姓名")
    var real_name: String? = null

    @ExcelIgnore
    var fileName: String? = null

}
