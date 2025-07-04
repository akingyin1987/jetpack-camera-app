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
package com.google.jetpackcamera.feature.preview

import android.net.Uri

/**
 * This interface is determined before the Preview UI is launched and passed into PreviewScreen. The
 * UX differs depends on which mode the Preview is launched under.
 */
sealed interface PreviewMode {
    /**
     * The default mode for the app.
     * 默认模式，
     */
    data class StandardMode(
        val onImageCapture: (PreviewViewModel.ImageCaptureEvent) -> Unit
    ) : PreviewMode

    /**
     * 外部图像捕获模式，外部视频捕获模式和外部多图像捕获模式的区别在于，它们都是在外部意图下启动的。
     * Under this mode, the app is launched by an external intent to capture one image.
     */
    data class ExternalImageCaptureMode(
        val imageCaptureUri: Uri?,
        val onImageCapture: (PreviewViewModel.ImageCaptureEvent) -> Unit
    ) : PreviewMode

    /**
     * 外部视频捕获模式，
     * Under this mode, the app is launched by an external intent to capture a video.
     */
    data class ExternalVideoCaptureMode(
        val videoCaptureUri: Uri?,
        val onVideoCapture: (PreviewViewModel.VideoCaptureEvent) -> Unit
    ) : PreviewMode

    /**
     * Under this mode, the app is launched by an external intent to capture multiple images.
     * 外部多图像捕获模式，在外部意图下启动以捕获多个图像。
     */
    data class ExternalMultipleImageCaptureMode(
        val imageCaptureUris: List<Uri>?,
        val onImageCapture: (PreviewViewModel.ImageCaptureEvent, Int) -> Unit
    ) : PreviewMode
}
