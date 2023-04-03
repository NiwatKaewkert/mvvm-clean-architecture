package me.niwat.mvvm.presenter.camera

import android.util.Log
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import me.niwat.mvvm.base.BaseViewModel

class CameraFragmentViewModel : BaseViewModel() {
    fun detectFaces(image: InputImage, imageProxy: ImageProxy) {
        // Real-time contour detection
        val realTimeOps = FaceDetectorOptions.Builder()
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
            .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
            .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
            .setMinFaceSize(0.15f)
            .enableTracking()
            .build()

        // get detector
        val detector = FaceDetection.getClient(realTimeOps)

        // run the detector

        // run the detector
        val result = detector.process(image)
            .addOnSuccessListener { faces -> // Task completed successfully
                for (face in faces) {
                    Log.d("niwat", "Found")
                }
                imageProxy.close()
            }
            .addOnFailureListener { e ->
                Log.d("niwat", "Failed in detecting the face ..." + e.message)
                e.printStackTrace()
                imageProxy.close()
            }
    }
}