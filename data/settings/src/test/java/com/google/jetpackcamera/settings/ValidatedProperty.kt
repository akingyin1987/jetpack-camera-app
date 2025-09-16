/*
 * Copyright (C) 2025 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.jetpackcamera.settings

/**
 *
 * @author: aking <a href="mailto:akingyin@163.com">Contact me.</a>
 * @since: 2025/9/15 10:30
 * @version: 1.0
 */
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type
import kotlin.contracts.ExperimentalContracts
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty
import kotlin.contracts.contract

class ValidatedProperty<T>(
    private var value: T?,
    private val validator: (T?) -> Boolean,
    private val errorMessage: (String) -> String = { "Property '$it' validation failed" }
) : ReadWriteProperty<Any?, T> {

    @OptIn(ExperimentalContracts::class)
    private fun validate(value: Any?): Boolean {
        contract {
            returns(true) implies (value != null)
        }
        @Suppress("UNCHECKED_CAST")
        return validator(value as T?)
    }

    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
        require(validate(value)) { errorMessage(property.name) }
        return value!!
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {

        this.value = value
    }



    // 获取当前值（可能为null）
    fun getValueOrNull(): T? = value

    // 检查是否有效
    fun isValid(): Boolean = validator(value)
}

// 工厂函数
fun <T> validatedProperty(
    initialValue: T? = null,
    validator: (T?) -> Boolean = { it != null },
    errorMessage: (String) -> String = { "Property '$it' cannot be null" }
) = ValidatedProperty(initialValue, validator, errorMessage)



class TestData{
    var name: String by validatedProperty()
}

// 使用示例
fun exampleUsage() {
    // 非空字符串
    var name: String by validatedProperty<String>()

    // 非空且非空字符串
    var description: String by validatedProperty(
        validator = { it != null && it.isNotBlank() },
        errorMessage = { "Property '$it' cannot be null or blank" }
    )

    // 正整数
    var age: Int by validatedProperty(
        validator = { it != null && it > 0 },
        errorMessage = { "Property '$it' must be a positive integer" }
    )

    // 使用
    name = "John"
    println(name) // 输出: John


    try {
        println(name) // 抛出异常
    } catch (e: IllegalArgumentException) {
        println("Error: ${e.message}") // 处理异常
    }
}

class TestDto{
    var name: String = ""

    var age: Int? = null
}
fun main() {
    val gson = Gson().newBuilder().create()
    val json = gson.toJson(TestDto())
    println(json)
//
//    val listType : Type = object : TypeToken<List<TestDto>>(){}.type
//
//    val testDto = gson.fromJson(json, TestDto::class.java)
    val testDto = gson.fromJson("{\"name\":null}", TestDto::class.java)

    println(null == testDto.name)
    println("null" == testDto.name)
    println(testDto.name)
}