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
package com.google.jetpackcamera.core.camera

import androidx.camera.core.CameraInfo
import com.google.jetpackcamera.model.AspectRatio
import com.google.jetpackcamera.model.CaptureMode
import com.google.jetpackcamera.model.DeviceRotation
import com.google.jetpackcamera.model.DynamicRange
import com.google.jetpackcamera.model.FlashMode
import com.google.jetpackcamera.model.ImageOutputFormat
import com.google.jetpackcamera.model.LensFacing
import com.google.jetpackcamera.model.StabilizationMode
import com.google.jetpackcamera.model.StreamConfig
import com.google.jetpackcamera.model.TestPattern
import com.google.jetpackcamera.model.VideoQuality

/**
 * 相机设置，只要相机正在运行就会持续存在
 * Camera settings that persist as long as a camera is running.
 *
 * Any change in these settings will require calling [ProcessCameraProvider.runWith] with
 * updates [CameraSelector] and/or [UseCaseGroup]
 * 这些设置中的任何更改都需要调用 [ProcessCameraProvider.runWith] 并更新
 * [CameraSelector] 和/或 [UseCaseGroup]
 */
internal sealed interface PerpetualSessionSettings {

    /**
     * 相机预览和拍摄输出的宽高比
     */
    val aspectRatio: AspectRatio

    /**
     * 拍摄模式，指示可以捕获的媒体类型（例如，仅照片、仅视频、照片和视频）
     */
    val captureMode: CaptureMode


    /**
     * 单相机操作模式的设置
     *
     * @property aspectRatio 相机预览和拍摄输出的宽高比
     * @property captureMode 拍摄模式，指示可以捕获的媒体类型
     * @property streamConfig 流配置（单流或多流）
     * @property targetFrameRate 视频录制的目标帧率
     * @property stabilizationMode 稳定模式（关闭、自动、开启、高质量、光学）
     * @property dynamicRange 动态范围设置（SDR、HLG、HDR10）
     * @property videoQuality 视频质量设置
     * @property imageFormat 拍摄图像的格式（JPEG等）
     */
    data class SingleCamera(
        override val aspectRatio: AspectRatio,
        override val captureMode: CaptureMode,
        val streamConfig: StreamConfig,
        val targetFrameRate: Int,
        val stabilizationMode: StabilizationMode,
        val dynamicRange: DynamicRange,
        val videoQuality: VideoQuality,
        val imageFormat: ImageOutputFormat
    ) : PerpetualSessionSettings

    /**
     * @property captureMode is always [com.google.jetpackcamera.model.CaptureMode.VIDEO_ONLY] in Concurrent Camera mode.
     * Concurrent Camera currently only supports video capture
     */
    /**
     * 并发相机操作模式的设置（同时使用多个相机）
     *
     * @property captureMode 在并发相机模式下始终是 [com.google.jetpackcamera.model.CaptureMode.VIDEO_ONLY]。
     * 并发相机目前仅支持视频捕获
     * @property primaryCameraInfo 主相机信息
     * @property secondaryCameraInfo 辅相机信息
     * @property aspectRatio 相机预览和拍摄输出的宽高比
     */
    data class ConcurrentCamera(
        val primaryCameraInfo: CameraInfo,
        val secondaryCameraInfo: CameraInfo,
        override val aspectRatio: AspectRatio
    ) : PerpetualSessionSettings {
        /**
         * 在并发相机模式下，仅支持视频捕获
         */
        override val captureMode: CaptureMode = CaptureMode.VIDEO_ONLY
    }
}

/**
 * Camera settings that can change while the camera is running.
 *
 * Any changes in these settings can be applied either directly to use cases via their
 * setter methods or to [androidx.camera.core.CameraControl].
 * The use cases typically will not need to be re-bound.
 * /**
 *  * 相机运行期间可以更改的设置
 *  *
 *  * 这些设置的任何更改都可以直接通过其setter方法应用于用例或应用于
 *  * [androidx.camera.core.CameraControl]。通常不需要重新绑定用例。
 *  *
 *  * @property isAudioEnabled 视频捕获是否启用音频录制
 *  * @property deviceRotation 设备当前的旋转角度
 *  * @property flashMode 当前的闪光灯模式设置
 *  * @property primaryLensFacing 主相机的朝向（前置或后置）
 *  * @property zoomRatios 每个镜头朝向的变焦比例
 *  * @property testPattern 要显示的测试图案（如果有）
 *  */
 */
internal data class TransientSessionSettings(
    val isAudioEnabled: Boolean,
    val deviceRotation: DeviceRotation,
    val flashMode: FlashMode,
    val primaryLensFacing: LensFacing,
    val zoomRatios: Map<LensFacing, Float>,
    val testPattern: TestPattern
)

/**
 * 开始视频录制的初始设置
 *
 * @property isAudioEnabled 视频是否应该录制音频
 * @property lensFacing 录制使用的镜头朝向（前置或后置）
 * @property zoomRatios 录制开始时每个镜头朝向的变焦比例
 */
data class InitialRecordingSettings(
    val isAudioEnabled: Boolean,
    val lensFacing: LensFacing,
    val zoomRatios: Map<LensFacing, Float>
)
