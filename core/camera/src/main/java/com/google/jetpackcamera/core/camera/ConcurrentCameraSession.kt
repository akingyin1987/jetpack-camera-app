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

import android.annotation.SuppressLint
import android.util.Log
import androidx.camera.core.CompositionSettings
import androidx.camera.core.TorchState
import androidx.lifecycle.asFlow
import com.google.jetpackcamera.settings.model.DynamicRange
import com.google.jetpackcamera.settings.model.ImageOutputFormat
import com.google.jetpackcamera.settings.model.StabilizationMode
import com.google.jetpackcamera.settings.model.VideoQuality
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val TAG = "ConcurrentCameraSession"

context(CameraSessionContext)
@SuppressLint("RestrictedApi")
internal suspend fun runConcurrentCameraSession(
    sessionSettings: PerpetualSessionSettings.ConcurrentCamera,
    useCaseMode: CameraUseCase.UseCaseMode
) = coroutineScope {
    val primaryLensFacing = sessionSettings.primaryCameraInfo.appLensFacing
    val secondaryLensFacing = sessionSettings.secondaryCameraInfo.appLensFacing
    Log.d(
        TAG,
        "Starting new concurrent camera session " +
            "[primary: $primaryLensFacing, secondary: $secondaryLensFacing]"
    )

    val initialTransientSettings = transientSettings
        .filterNotNull()
        .first()

    val videoCapture = if (useCaseMode != CameraUseCase.UseCaseMode.IMAGE_ONLY) {
        createVideoUseCase(
            cameraProvider.getCameraInfo(
                initialTransientSettings.primaryLensFacing.toCameraSelector()
            ),
            sessionSettings.aspectRatio,
            TARGET_FPS_AUTO,
            StabilizationMode.OFF,
            DynamicRange.SDR,
            VideoQuality.UNSPECIFIED,
            backgroundDispatcher
        )
    } else {
        null
    }

    val useCaseGroup = createUseCaseGroup(
        cameraInfo = sessionSettings.primaryCameraInfo,
        initialTransientSettings = initialTransientSettings,
        stabilizationMode = StabilizationMode.OFF,
        aspectRatio = sessionSettings.aspectRatio,
        dynamicRange = DynamicRange.SDR,
        imageFormat = ImageOutputFormat.JPEG,
        useCaseMode = useCaseMode,
        videoCaptureUseCase = videoCapture
    )

    val cameraConfigs = listOf(
        Pair(
            sessionSettings.primaryCameraInfo.cameraSelector,
            CompositionSettings.Builder()
                .setAlpha(1.0f)
                .setOffset(0.0f, 0.0f)
                .setScale(1.0f, 1.0f)
                .build()
        ),
        Pair(
            sessionSettings.secondaryCameraInfo.cameraSelector,
            CompositionSettings.Builder()
                .setAlpha(1.0f)
                .setOffset(2 / 3f - 0.1f, -2 / 3f + 0.1f)
                .setScale(1 / 3f, 1 / 3f)
                .build()
        )
    )

    cameraProvider.runWithConcurrent(cameraConfigs, useCaseGroup) { concurrentCamera ->
        Log.d(TAG, "Concurrent camera session started")
        val primaryCamera = concurrentCamera.cameras.first {
            it.cameraInfo.appLensFacing == sessionSettings.primaryCameraInfo.appLensFacing
        }

        launch {
            processFocusMeteringEvents(primaryCamera.cameraControl)
        }

        launch {
            processVideoControlEvents(
                useCaseGroup.getVideoCapture(),
                captureTypeSuffix = "DualCam"
            )
        }

        launch {
            sessionSettings.primaryCameraInfo.torchState.asFlow().collectLatest { torchState ->
                currentCameraState.update { old ->
                    old.copy(torchEnabled = torchState == TorchState.ON)
                }
            }
        }

        applyDeviceRotation(initialTransientSettings.deviceRotation, useCaseGroup)
        processTransientSettingEvents(
            primaryCamera,
            useCaseGroup,
            initialTransientSettings,
            transientSettings
        )
    }
}
