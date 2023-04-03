package me.niwat.mvvm.presenter.camera

import android.graphics.Bitmap
import android.util.Log
import androidx.camera.core.ImageProxy
import androidx.lifecycle.MutableLiveData
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import me.niwat.mvvm.base.BaseViewModel

class CameraFragmentViewModel : BaseViewModel() {
    val isDetected: MutableLiveData<Boolean> = MutableLiveData()

    fun detectFaces(image: Bitmap, imageProxy: ImageProxy) {
        // Real-time contour detection
        val realTimeOps = FaceDetectorOptions.Builder()
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
            .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
            .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
            .setMinFaceSize(0.15f)
            .build()

        // get detector
        val detector = FaceDetection.getClient(realTimeOps)

        val x = InputImage.fromBitmap(image, 0)
        // run the detector
        val result =
            detector.process(InputImage.fromBitmap(image, imageProxy.imageInfo.rotationDegrees))
                .addOnSuccessListener { faces ->
                    // Task completed successfully
                    if (faces.isEmpty()) {
                        isDetected.value = false
                    } else {
                        for (face in faces) {
                            Log.d("niwat", "Found")
                            isDetected.value = true
                        }
                    }
                    imageProxy.close()
                }
                .addOnFailureListener { e ->
                    Log.d("niwat", "Failed in detecting the face ..." + e.message)
                    isDetected.value = false
                    e.printStackTrace()
                    imageProxy.close()
                }
    }
}