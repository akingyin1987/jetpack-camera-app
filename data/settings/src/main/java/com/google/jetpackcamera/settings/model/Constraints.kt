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
package com.google.jetpackcamera.settings.model

import android.util.Range
import com.google.jetpackcamera.model.DynamicRange
import com.google.jetpackcamera.model.FlashMode
import com.google.jetpackcamera.model.Illuminant
import com.google.jetpackcamera.model.ImageOutputFormat
import com.google.jetpackcamera.model.LensFacing
import com.google.jetpackcamera.model.StabilizationMode
import com.google.jetpackcamera.model.StreamConfig
import com.google.jetpackcamera.model.TestPattern
import com.google.jetpackcamera.model.VideoQuality

/**
 * Represents the overall constraints and capabilities of the camera system on the device.
 *
 * This data class aggregates information about available lenses, support for concurrent
 * camera usage, and detailed constraints for each individual camera lens. It serves as a
 * central point for querying what features and settings are supported by the device's
 * camera hardware and software stack.
 *
 * @property availableLenses A list of [com.google.jetpackcamera.model.LensFacing] values indicating which camera lenses
 *                           (e.g., front, back) are available on the device.
 * @property concurrentCamerasSupported A boolean indicating whether the device supports
 *                                      operating multiple cameras concurrently.
 * @property perLensConstraints A map where each key is a [com.google.jetpackcamera.model.LensFacing] value and the
 *                              corresponding value is a [CameraConstraints] object
 *                              detailing the specific capabilities and limitations of that lens.
 */

/**
 * 表示设备上相机系统的整体约束和功能。
 *
 * 这个数据类聚合了有关可用镜头、并发相机使用支持以及每个单独相机镜头的详细约束信息。
 * 它作为查询设备相机硬件和软件堆栈支持的功能和设置的中心点。
 *
 * @property availableLenses [com.google.jetpackcamera.model.LensFacing] 值列表，指示设备上可用的相机镜头（例如前置、后置相机）
 * @property concurrentCamerasSupported 布尔值，指示设备是否支持同时操作多个相机
 * @property perLensConstraints Map集合，其中每个键是 [com.google.jetpackcamera.model.LensFacing] 值，
 *                              对应的值是一个 [CameraConstraints] 对象，详细说明该镜头的功能和限制
 */
data class CameraSystemConstraints(
    /**
     * 设备上可用的相机镜头列表
     * 通常包括前置和后置摄像头（如果可用）
     * @see LensFacing
     */
    val availableLenses: List<LensFacing> = emptyList(),
    /**
     * 指示设备是否支持同时使用多个摄像头
     * 当为true时，应用程序可以同时打开和使用多个相机镜头
     */
    val concurrentCamerasSupported: Boolean = false,

    /**
     * 每个可用镜头的约束映射
     * 每个条目都包含有关特定镜头朝向的详细功能信息
     * @see CameraConstraints
     */
    val perLensConstraints: Map<LensFacing, CameraConstraints> = emptyMap()
)


/**
 * 根据指定的选择器函数从所有镜头约束中提取并合并特定类型的数据
 *
 * @param T 要提取的数据类型
 * @param constraintSelector 从CameraConstraints中选择特定数据的函数
 * @return 包含所有镜头中指定类型数据的集合
 */
inline fun <reified T> CameraSystemConstraints.forDevice(
    crossinline constraintSelector: (CameraConstraints) -> Iterable<T>
) = perLensConstraints.values.asSequence().flatMap { constraintSelector(it) }.toSet()

/**
 * Defines the specific capabilities, limitations, and supported settings for a single camera lens.
 *
 * This data class encapsulates various constraints related to video and image capture for a
 * particular camera, such as supported stabilization modes, frame rates, dynamic ranges,
 * image formats, and zoom capabilities.
 *
 * @property supportedStabilizationModes A set of [com.google.jetpackcamera.model.StabilizationMode] values that are supported
 *                                       by this camera lens.
 * @property supportedFixedFrameRates A set of integers representing fixed frame rates (FPS)
 *                                    supported for video recording with this lens.
 *                                    May include values like [FPS_AUTO], [FPS_15], [FPS_30], [FPS_60].
 * @property supportedDynamicRanges A set of [com.google.jetpackcamera.model.DynamicRange] values (e.g., SDR, HDR10) that
 *                                  this camera lens can capture.
 * @property supportedVideoQualitiesMap A map where keys are [com.google.jetpackcamera.model.DynamicRange] values and values
 *                                      are lists of [VideoQuality] settings supported for that
 *                                      dynamic range.
 * @property supportedImageFormatsMap A map where keys are [com.google.jetpackcamera.model.StreamConfig] values (indicating single
 *                                    or multi-stream configurations) and values are sets of
 *                                    [ImageOutputFormat] (e.g., JPEG, DNG) supported for that
 *                                    stream configuration.
 * @property supportedIlluminants A set of [com.google.jetpackcamera.model.Illuminant] values supported by this camera, typically
 *                                indicating the type of flash unit available (e.g., FLASH_UNIT).
 * @property supportedFlashModes A set of [com.google.jetpackcamera.model.FlashMode] values (e.g., OFF, ON, AUTO) that can be
 *                               used with this camera lens.
 * @property supportedZoomRange An optional [Range] of floats indicating the minimum and maximum
 *                              zoom ratios supported by this lens. Null if zoom is not supported
 *                              or the range is not available.
 * @property unsupportedStabilizationFpsMap A map where keys are [com.google.jetpackcamera.model.StabilizationMode] values and
 *                                          values are sets of frame rates (FPS) that are
 *                                          *not* supported when that specific stabilization mode
 *                                          is active. This helps in understanding combinations
 *                                          that are disallowed.
 */

/**
 * 定义单个相机镜头的特定功能、限制和支持的设置。
 *
 * 这个数据类封装了与特定相机相关的各种约束，如支持的稳定模式、帧率、动态范围、
 * 图像格式和变焦功能等。
 *
 * @property supportedStabilizationModes 该相机镜头支持的 [com.google.jetpackcamera.model.StabilizationMode] 值集合
 * @property supportedFixedFrameRates 视频录制时该镜头支持的固定帧率(FPS)整数集合
 * @property supportedDynamicRanges 该相机镜头可以捕获的 [com.google.jetpackcamera.model.DynamicRange] 值集合（例如SDR、HDR10）
 * @property supportedVideoQualitiesMap Map集合，键为 [com.google.jetpackcamera.model.DynamicRange] 值，值为该动态范围支持的 [VideoQuality] 设置列表
 * @property supportedImageFormatsMap Map集合，键为 [com.google.jetpackcamera.model.StreamConfig] 值（表示单流或多流配置），值为该流配置支持的 [ImageOutputFormat] 集合
 * @property supportedIlluminants 该相机支持的 [com.google.jetpackcamera.model.Illuminant] 值集合，通常表示可用的闪光灯类型（例如FLASH_UNIT）
 * @property supportedFlashModes 可与此相机镜头一起使用的 [com.google.jetpackcamera.model.FlashMode] 值集合（例如OFF、ON、AUTO）
 * @property supportedZoomRange 可选的浮点数 [Range]，表示该镜头支持的最小和最大变焦比率，如果不支持变焦或范围不可用则为null
 * @property unsupportedStabilizationFpsMap Map集合，键为 [com.google.jetpackcamera.model.StabilizationMode] 值，值为当该特定稳定模式激活时不支持的帧率(FPS)集合
 * @property supportedTestPatterns 该相机支持的测试图案集合
 */
data class CameraConstraints(
    /**
     * 支持的图像稳定模式集合
     */
    val supportedStabilizationModes: Set<StabilizationMode>,

    /**
     * 支持的固定帧率集合
     * 可能包括 [FPS_AUTO]、[FPS_15]、[FPS_30]、[FPS_60] 等值
     */
    val supportedFixedFrameRates: Set<Int>,
    /**
     * 支持的动态范围集合
     * 例如SDR、HDR10等
     */
    val supportedDynamicRanges: Set<DynamicRange>,

    /**
     * 支持的视频质量映射
     * 键为动态范围，值为该动态范围支持的视频质量列表
     */
    val supportedVideoQualitiesMap: Map<DynamicRange, List<VideoQuality>>,

    /**
     * 支持的图像格式映射
     * 键为流配置（单流或多流），值为该配置支持的图像输出格式集合
     */
    val supportedImageFormatsMap: Map<StreamConfig, Set<ImageOutputFormat>>,

    /**
     * 支持的光源类型集合
     */
    val supportedIlluminants: Set<Illuminant>,

    /**
     * 支持的闪光灯模式集合
     */
    val supportedFlashModes: Set<FlashMode>,

    /**
     * 支持的变焦范围
     * 一个浮点数范围，表示最小和最大变焦比率，如果变焦不受支持则为null
     */
    val supportedZoomRange: Range<Float>?,
    /**
     * 不支持的稳定帧率映射
     * 键为稳定模式，值为该稳定模式下不支持的帧率集合
     */
    val unsupportedStabilizationFpsMap: Map<StabilizationMode, Set<Int>>,

    /**
     * 支持的测试图案集合
     */
    val supportedTestPatterns: Set<TestPattern>
) {
    val StabilizationMode.unsupportedFpsSet
        get() = unsupportedStabilizationFpsMap[this] ?: emptySet()

    companion object {
        const val FPS_AUTO = 0
        const val FPS_15 = 15
        const val FPS_30 = 30
        const val FPS_60 = 60
    }
}

/**
 * Useful set of constraints for testing
 */

/**
 * 用于测试的典型系统约束集合
 */
val TYPICAL_SYSTEM_CONSTRAINTS =
    CameraSystemConstraints(
        availableLenses = listOf(LensFacing.FRONT, LensFacing.BACK),
        concurrentCamerasSupported = false,
        perLensConstraints = buildMap {
            for (lensFacing in listOf(LensFacing.FRONT, LensFacing.BACK)) {
                put(
                    lensFacing,
                    CameraConstraints(
                        supportedFixedFrameRates = setOf(15, 30),
                        supportedStabilizationModes = setOf(StabilizationMode.OFF),
                        supportedDynamicRanges = setOf(DynamicRange.SDR),
                        supportedImageFormatsMap = mapOf(
                            Pair(StreamConfig.SINGLE_STREAM, setOf(ImageOutputFormat.JPEG)),
                            Pair(StreamConfig.MULTI_STREAM, setOf(ImageOutputFormat.JPEG))
                        ),
                        supportedVideoQualitiesMap = emptyMap(),
                        supportedIlluminants = setOf(Illuminant.FLASH_UNIT),
                        supportedFlashModes = setOf(FlashMode.OFF, FlashMode.ON, FlashMode.AUTO),
                        supportedZoomRange = Range(.5f, 10f),
                        unsupportedStabilizationFpsMap = emptyMap(),
                        supportedTestPatterns = setOf(TestPattern.Off)
                    )
                )
            }
        }
    )
