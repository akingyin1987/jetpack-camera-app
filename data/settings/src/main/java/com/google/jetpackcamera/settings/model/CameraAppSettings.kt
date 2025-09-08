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

import com.google.jetpackcamera.model.AspectRatio
import com.google.jetpackcamera.model.CaptureMode
import com.google.jetpackcamera.model.ConcurrentCameraMode
import com.google.jetpackcamera.model.DarkMode
import com.google.jetpackcamera.model.DebugSettings
import com.google.jetpackcamera.model.DeviceRotation
import com.google.jetpackcamera.model.DynamicRange
import com.google.jetpackcamera.model.FlashMode
import com.google.jetpackcamera.model.ImageOutputFormat
import com.google.jetpackcamera.model.LensFacing
import com.google.jetpackcamera.model.StabilizationMode
import com.google.jetpackcamera.model.StreamConfig
import com.google.jetpackcamera.model.VideoQuality

const val TARGET_FPS_AUTO = 0
const val UNLIMITED_VIDEO_DURATION = 0L

/**
 * Data layer representation for settings.
 *  * @property captureMode 拍摄模式，如标准模式或专业模式
 *  * @property cameraLensFacing 相机镜头朝向，前置或后置
 *  * @property darkMode 深色模式设置，如系统默认、浅色或深色
 *  * @property flashMode 闪光灯模式，开启、关闭或自动
 *  * @property streamConfig 流配置，定义如何处理相机数据流
 *  * @property aspectRatio 纵横比，如 4:3, 16:9 等
 *  * @property stabilizationMode 稳定模式，如自动、开启或关闭
 *  * @property dynamicRange 动态范围，如 SDR 或 HDR
 *  * @property videoQuality 视频质量设置
 *  * @property defaultZoomRatios 默认缩放比例，按镜头朝向分别设置
 *  * @property targetFrameRate 目标帧率，0表示自动
 *  * @property imageFormat 图像输出格式，如 JPEG 或 PNG
 *  * @property audioEnabled 是否启用音频录制
 *  * @property deviceRotation 设备旋转方向
 *  * @property concurrentCameraMode 并发相机模式，用于同时使用前后摄像头
 *  * @property maxVideoDurationMillis 视频最大时长限制，0表示无限制
 *  * @property debugSettings 调试设置
 */
data class CameraAppSettings(
    val captureMode: CaptureMode = CaptureMode.STANDARD,
    val cameraLensFacing: LensFacing = LensFacing.BACK,
    val darkMode: DarkMode = DarkMode.SYSTEM,
    val flashMode: FlashMode = FlashMode.OFF,
    val streamConfig: StreamConfig = StreamConfig.MULTI_STREAM,
    val aspectRatio: AspectRatio = AspectRatio.NINE_SIXTEEN,
    val stabilizationMode: StabilizationMode = StabilizationMode.AUTO,
    val dynamicRange: DynamicRange = DynamicRange.SDR,
    val videoQuality: VideoQuality = VideoQuality.UNSPECIFIED,
    val defaultZoomRatios: Map<LensFacing, Float> = mapOf(),
    val targetFrameRate: Int = TARGET_FPS_AUTO,
    val imageFormat: ImageOutputFormat = ImageOutputFormat.JPEG,
    val audioEnabled: Boolean = true,
    val deviceRotation: DeviceRotation = DeviceRotation.Natural,
    val concurrentCameraMode: ConcurrentCameraMode = ConcurrentCameraMode.OFF,
    val maxVideoDurationMillis: Long = UNLIMITED_VIDEO_DURATION,
    val debugSettings: DebugSettings = DebugSettings()
)

/**
 * 根据当前镜头朝向获取相机约束条件
 *
 * @param cameraAppSettings
 * @return
 */
fun CameraSystemConstraints.forCurrentLens(
    cameraAppSettings: CameraAppSettings
): CameraConstraints? = perLensConstraints[cameraAppSettings.cameraLensFacing]

/**
 * 默认的相机应用设置实例
 */
val DEFAULT_CAMERA_APP_SETTINGS = CameraAppSettings()
