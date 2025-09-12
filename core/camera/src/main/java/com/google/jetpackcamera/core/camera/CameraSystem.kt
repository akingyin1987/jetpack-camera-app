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
package com.google.jetpackcamera.core.camera

import android.content.ContentResolver
import androidx.camera.core.ImageCapture
import androidx.camera.core.SurfaceRequest
import com.google.jetpackcamera.model.AspectRatio
import com.google.jetpackcamera.model.CameraZoomRatio
import com.google.jetpackcamera.model.CaptureMode
import com.google.jetpackcamera.model.ConcurrentCameraMode
import com.google.jetpackcamera.model.DeviceRotation
import com.google.jetpackcamera.model.DynamicRange
import com.google.jetpackcamera.model.FlashMode
import com.google.jetpackcamera.model.ImageOutputFormat
import com.google.jetpackcamera.model.LensFacing
import com.google.jetpackcamera.model.SaveLocation
import com.google.jetpackcamera.model.StabilizationMode
import com.google.jetpackcamera.model.StreamConfig
import com.google.jetpackcamera.model.TestPattern
import com.google.jetpackcamera.model.VideoQuality
import com.google.jetpackcamera.settings.model.CameraAppSettings
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.flow.StateFlow

/**
 * 相机数据层接口
 * Data layer for camera.
 */
interface CameraSystem {
    /**
     * 使用提供的设置初始化相机
     * Initializes the camera.
     *
     * @return list of available lenses.
     */
    suspend fun initialize(
        cameraAppSettings: CameraAppSettings,
        cameraPropertiesJSONCallback: (result: String) -> Unit
    )

    /**
     * Starts the camera.
     *
     * This will start to configure the camera, but frames won't stream until a [SurfaceRequest]
     * from [getSurfaceRequest] has been fulfilled.
     *
     * The camera will run until the calling coroutine is cancelled.
     */

    /**
     * 启动相机
     *
     * 这将开始配置相机，但在[getSurfaceRequest]返回的[SurfaceRequest]被满足之前不会开始传输帧。
     *
     * 相机会一直运行直到调用协程被取消。
     */
    suspend fun runCamera()

    /**
     * 使用相机拍照，拍摄过程开始时调用的回调函数
     *
     * @param onCaptureStarted
     */
    suspend fun takePicture(onCaptureStarted: (() -> Unit) = {})

    /**
     * Takes a picture with the camera. If ignoreUri is set to true, the picture taken will be saved
     * at the default directory for pictures on device. Otherwise, it will be saved at the uri
     * location if the uri is not null. If it is null, an error will be thrown.
     */
    /**
     * 使用相机拍照。如果ignoreUri设置为true，拍摄的照片将保存在设备上的默认图片目录中。
     * 否则，如果uri不为空，将保存在uri指定的位置。如果uri为空，则抛出错误。
     *
     * @param contentResolver 用于保存图像的内容解析器
     * @param saveLocation 图像应保存的位置
     * @param onCaptureStarted 拍摄过程开始时调用的回调函数
     * @return 包含有关已保存图像信息的ImageCapture.OutputFileResults
     */
    suspend fun takePicture(
        contentResolver: ContentResolver,
        saveLocation: SaveLocation,
        onCaptureStarted: (() -> Unit) = {}
    ): ImageCapture.OutputFileResults


    /**
     * 开始录制视频到指定位置
     *
     * @param saveLocation
     * @param onVideoRecord
     */
    suspend fun startVideoRecording(
        saveLocation: SaveLocation,
        onVideoRecord: (OnVideoRecordEvent) -> Unit
    )

    /**
     * 暂停当前视频录制
     *
     */
    suspend fun pauseVideoRecording()

    /**
     * 恢复当前视频录制
     *
     */
    suspend fun resumeVideoRecording()


    /**
     * 停止当前视频录制
     *
     */
    suspend fun stopVideoRecording()


    /**
     * 更改相机的缩放比例
     *
     * @param newZoomState
     */
    fun changeZoomRatio(newZoomState: CameraZoomRatio)

    /**
     * 为相机预览设置测试图案
     *
     * @param newTestPattern
     */
    fun setTestPattern(newTestPattern: TestPattern)

    /**
     * 获取相机当前状态的StateFlow
     *
     * @return
     */
    fun getCurrentCameraState(): StateFlow<CameraState>

    /**
     * 获取当前surface请求的StateFlow
     *
     * @return
     */
    fun getSurfaceRequest(): StateFlow<SurfaceRequest?>

    /**
     * 获取屏幕闪光事件的通道
     *
     * @return
     */
    fun getScreenFlashEvents(): ReceiveChannel<ScreenFlashEvent>

    /**
     * 获取当前相机设置的StateFlow
     *
     * @return
     */
    fun getCurrentSettings(): StateFlow<CameraAppSettings?>

    /**
     * 设置相机的闪光模式
     *
     * @param flashMode
     */
    fun setFlashMode(flashMode: FlashMode)


    /**
     * 检查屏幕闪光是否启用
     *
     * @return
     */
    fun isScreenFlashEnabled(): Boolean


    /**
     * 设置相机的宽高比
     *
     * @param aspectRatio
     */
    suspend fun setAspectRatio(aspectRatio: AspectRatio)

    /**
     * 设置录制视频的质量
     *
     * @param videoQuality
     */
    suspend fun setVideoQuality(videoQuality: VideoQuality)


    /**
     * 设置镜头朝向（前置或后置摄像头）
     *
     * @param lensFacing
     */
    suspend fun setLensFacing(lensFacing: LensFacing)

    /**
     * 在指定坐标执行点击对焦
     *
     * @param x 对焦的x坐标
     * @param y 对焦的y坐标
     */
    suspend fun tapToFocus(x: Float, y: Float)


    /**
     * 设置相机的流配置
     *
     * @param streamConfig
     */
    suspend fun setStreamConfig(streamConfig: StreamConfig)


    /**
     * 设置相机的动态范围
     *
     * @param dynamicRange
     */
    suspend fun setDynamicRange(dynamicRange: DynamicRange)


    /**
     * 设置设备旋转以确保图像方向正确
     *
     * @param deviceRotation
     */
    fun setDeviceRotation(deviceRotation: DeviceRotation)


    /**
     * 设置并发相机模式（适用于具有多个摄像头的设备）
     *
     * @param concurrentCameraMode
     */
    suspend fun setConcurrentCameraMode(concurrentCameraMode: ConcurrentCameraMode)

    /**
     * 设置图像输出格式
     *
     * @param imageFormat
     */
    suspend fun setImageFormat(imageFormat: ImageOutputFormat)


    /**
     * 启用或禁用视频的音频录制
     *
     * @param isAudioEnabled
     */
    suspend fun setAudioEnabled(isAudioEnabled: Boolean)


    /**
     * 设置相机的稳定模式
     *
     * @param stabilizationMode
     */
    suspend fun setStabilizationMode(stabilizationMode: StabilizationMode)


    /**
     * 设置视频录制的目标帧率
     *
     * @param targetFrameRate
     */
    suspend fun setTargetFrameRate(targetFrameRate: Int)


    /**
     * 设置视频录制的最长持续时间（以毫秒为单位）
     *
     * @param durationInMillis
     */
    suspend fun setMaxVideoDuration(durationInMillis: Long)


    /**
     * 设置相机的拍摄模式（例如，单拍、连拍等）
     *
     * @param captureMode
     */
    suspend fun setCaptureMode(captureMode: CaptureMode)

    /**
     * Represents the events required for screen flash.
     */
    /**
     * 表示屏幕闪光所需事件的数据类
     *
     * 屏幕闪光是一种在低光条件下拍照时临时亮起整个屏幕的功能，作为硬件闪光灯的替代方案。
     */
    data class ScreenFlashEvent(val type: Type, val onComplete: () -> Unit) {
        enum class Type {
            APPLY_UI,
            CLEAR_UI
        }
    }
}
