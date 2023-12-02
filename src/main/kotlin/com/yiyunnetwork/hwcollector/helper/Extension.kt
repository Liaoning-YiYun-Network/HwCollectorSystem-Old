package com.yiyunnetwork.hwcollector.helper

import java.io.File

fun File.getFileTree(): String {
    val tree = StringBuilder()
    if (this.isDirectory) {
        tree.append(this.name).append("\n")
        this.listFiles()?.forEach {
            tree.append(it.getFileTree())
        }
    } else {
        tree.append(this.name).append("\n")
    }
    return tree.toString()
}