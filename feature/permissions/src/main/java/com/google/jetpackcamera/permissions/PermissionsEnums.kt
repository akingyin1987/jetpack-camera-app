/*
 * Copyright (C) 2024 The Android Open Source Project
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
package com.google.jetpackcamera.permissions

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.Mic
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.painterResource
import com.google.jetpackcamera.permissions.ui.CAMERA_PERMISSION_BUTTON
import com.google.jetpackcamera.permissions.ui.RECORD_AUDIO_PERMISSION_BUTTON

const val CAMERA_PERMISSION = "android.permission.CAMERA"
const val AUDIO_RECORD_PERMISSION = "android.permission.RECORD_AUDIO"

/**
 * // 定义一个密封接口 PermissionInfoProvider，用于提供与权限相关的信息
 * Helper class storing a permission's relevant UI information
 */
sealed interface PermissionInfoProvider {

    // @Composable 注解表明此方法可以在 Jetpack Compose 中使用，用于返回绘制权限图标的 Painter 对象
    @Composable
    fun getPainter(): Painter {
        // 从接口的实现中获取图标资源ID
        val iconResId = getDrawableResId()
        // 从接口的实现中获取矢量图标
        val iconVector = getImageVector()
        // 使用 require 函数确保图标资源ID和矢量图标中只有一个被设置（非空），否则抛出异常
        require((iconResId == null).xor(iconVector == null)) {
            "UI 项目应恰好设置 iconResId 或 iconVector 中的一个。"
        }
        // 如果图标资源ID非空，则使用 painterResource 函数创建并返回一个 Painter 对象
        // 如果图标资源ID为空但矢量图标非空，则使用 rememberVectorPainter 函数创建并返回一个 Painter 对象
        // !! 操作符在这里是安全的，因为我们已经通过 require 函数确保了至少有一个非空值
        return iconResId?.let { painterResource(it) }
            ?: iconVector?.let {
                rememberVectorPainter(
                    it
                )
            }!!
    }

    /**
     * 获取权限的字符串引用。
     *
     * @return 表示权限的字符串
     */
    fun getPermission(): String

    /**
     * 检查该权限是否是可选的。
     *
     * @return 如果权限是可选的，则返回 true；否则返回 false
     */
    fun isOptional(): Boolean

    fun getTestTag(): String

    /**
     * 获取权限图标的资源ID。
     *
     * @return 权限图标的资源ID，如果未设置则返回 null
     */
    @DrawableRes
    fun getDrawableResId(): Int?

    /**
     * 获取权限的矢量图标。
     *
     * @return 权限的矢量图标，如果未设置则返回 null
     */
    fun getImageVector(): ImageVector?

    /**
     * 获取权限标题的字符串资源ID。
     *
     * @return 权限标题的字符串资源ID
     */
    @StringRes
    fun getPermissionTitleResId(): Int

    /**
     * 获取权限主体文本的字符串资源ID。
     *
     * @return 权限主体文本的字符串资源ID
     */
    @StringRes
    fun getPermissionBodyTextResId(): Int

    /**
     * 获取权限说明主体文本的字符串资源ID（可选）。
     *
     * @return 权限说明主体文本的字符串资源ID，如果未设置则返回 null
     */
    @StringRes
    fun getRationaleBodyTextResId(): Int?

    /**
     * 获取图标无障碍文本的字符串资源ID。
     *
     * @return 图标无障碍文本的字符串资源ID
     */
    @StringRes
    fun getIconAccessibilityTextResId(): Int
}

/**
 * 权限信息提供者。
 * Implementation of [PermissionInfoProvider]
 * Supplies the information needed for a permission's UI screen
 */
enum class PermissionEnum : PermissionInfoProvider {

    CAMERA {
        override fun getPermission(): String = CAMERA_PERMISSION

        override fun isOptional(): Boolean = false

        override fun getDrawableResId(): Int? = null

        override fun getImageVector(): ImageVector = Icons.Outlined.CameraAlt

        override fun getPermissionTitleResId(): Int = R.string.camera_permission_screen_title


        override fun getTestTag(): String = CAMERA_PERMISSION_BUTTON

        override fun getPermissionBodyTextResId(): Int =
            R.string.camera_permission_required_rationale

        override fun getRationaleBodyTextResId(): Int =
            R.string.camera_permission_declined_rationale

        override fun getIconAccessibilityTextResId(): Int =
            R.string.camera_permission_accessibility_text
    },

    RECORD_AUDIO {
        override fun getPermission(): String = AUDIO_RECORD_PERMISSION

        override fun isOptional(): Boolean = true

        override fun getDrawableResId(): Int? = null

        override fun getImageVector(): ImageVector = Icons.Outlined.Mic

        override fun getPermissionTitleResId(): Int = R.string.microphone_permission_screen_title

        override fun getPermissionBodyTextResId(): Int =
            R.string.microphone_permission_required_rationale

        override fun getRationaleBodyTextResId(): Int? = null

        override fun getTestTag(): String =RECORD_AUDIO_PERMISSION_BUTTON

        override fun getIconAccessibilityTextResId(): Int =
            R.string.microphone_permission_accessibility_text
    }
}
