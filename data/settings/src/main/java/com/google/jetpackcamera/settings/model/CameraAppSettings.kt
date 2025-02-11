/*
 * Copyright (C) 2023 The Android Open Source Project
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
package com.google.jetpackcamera.settings.model

const val TARGET_FPS_AUTO = 0
const val UNLIMITED_VIDEO_DURATION = 0L
val DEFAULT_HDR_DYNAMIC_RANGE = DynamicRange.HLG10
val DEFAULT_HDR_IMAGE_OUTPUT = ImageOutputFormat.JPEG_ULTRA_HDR



/**
 * Data layer representation for settings.
 */

// 相机应用设置的数据类
data class CameraAppSettings(
    val captureMode: CaptureMode = CaptureMode.STANDARD,
    // 摄像头朝向：默认为后置摄像头
    val cameraLensFacing: LensFacing = LensFacing.BACK,

    // 暗黑模式：默认为系统默认设置
    val darkMode: DarkMode = DarkMode.SYSTEM,

    // 闪光灯模式：默认为关闭
    val flashMode: FlashMode = FlashMode.OFF,

    // 流配置：默认为多流配置（可能涉及预览流、拍照流等）
    val streamConfig: StreamConfig = StreamConfig.MULTI_STREAM,

    // 宽高比：默认为16:9
    val aspectRatio: AspectRatio = AspectRatio.NINE_SIXTEEN,

    // 稳定模式：默认为自动稳定
    val stabilizationMode: StabilizationMode = StabilizationMode.AUTO,

    // 动态范围：默认为标准动态范围（SDR）
    val dynamicRange: DynamicRange = DynamicRange.SDR,

    // 视频质量：默认为未指定，具体质量由其他设置决定
    val videoQuality: VideoQuality = VideoQuality.UNSPECIFIED,

    // 缩放比例：默认为1.0（即不缩放）
    val zoomScale: Float = 1f,

    // 目标帧率：默认为自动帧率（具体值由设备或库决定）
    val targetFrameRate: Int = TARGET_FPS_AUTO, // 假设TARGET_FPS_AUTO是一个预定义的常量

    // 图像输出格式：默认为JPEG格式
    val imageFormat: ImageOutputFormat = ImageOutputFormat.JPEG,

    // 音频启用状态：默认为启用
    val audioEnabled: Boolean = true,

    // 设备旋转：默认为自然旋转（即根据设备当前方向自动旋转）
    val deviceRotation: DeviceRotation = DeviceRotation.Natural,

    // 并发摄像头模式：默认为关闭（即一次只能使用一个摄像头）
    val concurrentCameraMode: ConcurrentCameraMode = ConcurrentCameraMode.OFF,

    // 最大视频时长（毫秒）：默认为无限制
    val maxVideoDurationMillis: Long = UNLIMITED_VIDEO_DURATION
)
//data class CameraAppSettings(
//    val cameraLensFacing: LensFacing = LensFacing.BACK,
//    val darkMode: DarkMode = DarkMode.SYSTEM,
//    val flashMode: FlashMode = FlashMode.OFF,
//    val streamConfig: StreamConfig = StreamConfig.MULTI_STREAM,
//    val aspectRatio: AspectRatio = AspectRatio.NINE_SIXTEEN,
//    val stabilizationMode: StabilizationMode = StabilizationMode.AUTO,
//    val dynamicRange: DynamicRange = DynamicRange.SDR,
//    val videoQuality: VideoQuality = VideoQuality.UNSPECIFIED,
//    val zoomScale: Float = 1f,
//    val targetFrameRate: Int = TARGET_FPS_AUTO,
//    val imageFormat: ImageOutputFormat = ImageOutputFormat.JPEG,
//    val audioEnabled: Boolean = true,
//    val deviceRotation: DeviceRotation = DeviceRotation.Natural,
//    val concurrentCameraMode: ConcurrentCameraMode = ConcurrentCameraMode.OFF,
//    val maxVideoDurationMillis: Long = UNLIMITED_VIDEO_DURATION
//)

fun SystemConstraints.forCurrentLens(cameraAppSettings: CameraAppSettings): CameraConstraints? =
    perLensConstraints[cameraAppSettings.cameraLensFacing]

val DEFAULT_CAMERA_APP_SETTINGS = CameraAppSettings()
