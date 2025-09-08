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
package com.google.jetpackcamera.model

/**
 * Class representing the app's configuration to capture an image
 * 定义了相机应用支持的不同拍摄模式，每种模式决定了可以执行的拍摄操作类型，以及用户交互的方式
 */
enum class CaptureMode {

    /**
     * 标准拍摄模式
     * 在此模式下，同时绑定图像和视频使用场景，支持拍照和录像功能
     * 用户交互:
     *      * - 点击拍摄按钮进行拍照
     *      * - 长按拍摄按钮开始录制视频，释放按钮完成录制
     * Both Image and Video use cases will be bound.
     *
     * Tap the Capture Button to take an image.
     *
     * Hold the Capture button to start recording, and release to complete the recording.
     */
    STANDARD,

    /**
     * 仅视频模式
     * Video use case will be bound. Image use case will not be bound.
     *
     * Tap the Capture Button to start recording.
     * Hold the Capture button to start recording; releasing will not stop the recording.
     *
     * Tap the capture button again after recording has started to complete the recording.
     */
    VIDEO_ONLY,

    /**
     * 仅图像模式
     * Image use case will be bound. Video use case will not be bound.
     *
     * Tap the Capture Button to capture an Image.
     * Holding the Capture Button will do nothing. Subsequent release of the Capture button will also do nothing.
     */
    IMAGE_ONLY
}
