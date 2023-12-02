package com.yiyunnetwork.hwcollector.helper


/**
 * Returns the default value when this instance represents the `null` result or the encapsulated value otherwise.
 *
 * This function is a shorthand for `getOrNull { default }` (see [Result.getOrNull]).
 */
fun <T> Result<T>.getWhenNull(default: T): T {
    val data = this.getOrNull()
    return data ?: default
}