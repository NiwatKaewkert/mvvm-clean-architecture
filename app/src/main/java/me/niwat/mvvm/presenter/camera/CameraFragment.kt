package me.niwat.mvvm.presenter.camera

import android.graphics.Bitmap
import android.graphics.Color
import android.media.Image
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import me.niwat.mvvm.R
import me.niwat.mvvm.base.BaseFragment
import me.niwat.mvvm.databinding.FragmentCameraBinding
import me.niwat.mvvm.utils.toBitmap
import me.niwat.mvvm.utils.toCircularBitmap
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import java.util.concurrent.Executor

class CameraFragment :
    BaseFragment<FragmentCameraBinding, CameraFragmentViewModel>(FragmentCameraBinding::inflate),
    ImageAnalysis.Analyzer {

    override val viewModel: CameraFragmentViewModel by activityViewModel()

    override fun init() {
    }

    override fun updateUI(view: View, savedInstanceState: Bundle?) {
        startCamera()
    }

    override fun observer() {
        viewModel.isDetected.observe(viewLifecycleOwner) {
            if (it) {
                binding.circleBorder.changeBorderColor(Color.GREEN)
                viewModel.textSuggestion.value = resources.getString(R.string.suggest_text_good)
            } else {
                binding.circleBorder.changeBorderColor(Color.RED)
                viewModel.textSuggestion.value =
                    resources.getString(R.string.suggest_text_position_your_face)
                viewModel.isDidCondition.value = false
            }
        }

        viewModel.isDidCondition.observe(viewLifecycleOwner) {
            if (it) {
                viewModel.textSuggestion.value = resources.getString(R.string.suggest_text_good2)
                viewLifecycleOwner.lifecycleScope.launch {
                    delay(2000)
                    activity?.supportFragmentManager?.popBackStack()
                }
            }
        }

        viewModel.textSuggestion.observe(viewLifecycleOwner) {
            binding.circleBorder.setText(it)
        }

        viewModel.rotY.observe(viewLifecycleOwner) { rotY ->
            if (rotY > 45 && viewModel.isDetected.value == true) {
                viewModel.isDidCondition.value = true
            }
        }
    }

    private fun startCamera() {
        binding.apply {
            val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
            cameraProviderFuture.addListener({
                // Used to bind the lifecycle of cameras to the lifecycle owner
                val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

                // Preview
                val preview = Preview.Builder().build().also {
                    it.setSurfaceProvider(cameraView.surfaceProvider)
                }

                // Select back camera as a default
                val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA

                // image analysis use case
                val imageAnalysis = ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST).build()

                imageAnalysis.setAnalyzer(getExecutor(), this@CameraFragment)

                try {
                    // Unbind use cases before rebinding
                    cameraProvider.unbindAll()
                    // Bind use cases to camera
                    cameraProvider.bindToLifecycle(
                        viewLifecycleOwner, cameraSelector, preview, imageAnalysis
                    )

                } catch (exc: Exception) {
                    Log.e(TAG, "Use case binding failed", exc)
                }

            }, getExecutor())
        }
    }

    private fun getExecutor(): Executor {
        return ContextCompat.getMainExecutor(requireContext())
    }

    private fun detectFaces(image: Bitmap, imageProxy: ImageProxy) {
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

        val x = InputImage.fromBitmap(image, 0)
        // run the detector
        val result =
            detector.process(InputImage.fromBitmap(image, imageProxy.imageInfo.rotationDegrees))
                .addOnSuccessListener { faces ->
                    // Task completed successfully
                    if (faces.isEmpty()) {
                        viewModel.isDetected.value = false
                    } else {
                        for (face in faces) {
                            val faceBoundingBox = face.boundingBox
                            val ovalShape = binding.circleBorder.getBounds()
                            ovalShape?.let {
                                if (faceBoundingBox.left > ovalShape.left &&
                                    faceBoundingBox.top > ovalShape.top &&
                                    faceBoundingBox.bottom < ovalShape.bottom &&
                                    faceBoundingBox.right < ovalShape.right &&
                                    viewModel.isDetected.value == false
                                ) {
                                    viewModel.isDetected.value = true
                                    viewModel.doCondition()
                                }
                            }
                            viewModel.rotY.value = face.headEulerAngleY
                            Log.d(
                                "kuy",
                                "${face.headEulerAngleY}"
                            )
                        }
                    }
                    imageProxy.close()
                }
                .addOnFailureListener { e ->
                    Log.d("niwat", "Failed in detecting the face ..." + e.message)
                    viewModel.isDetected.value = false
                    e.printStackTrace()
                    imageProxy.close()
                }

    }


    @ExperimentalGetImage
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage: Image? = imageProxy.image
        mediaImage?.let {
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
            val bmp = image.toBitmap()

            bmp?.let {
                val imageBitmap = bmp.toCircularBitmap()
                detectFaces(imageBitmap, imageProxy)
            }
        }
    }

    companion object {
        private var TAG = CameraFragment::class.java.simpleName
        fun newInstance(): Fragment = CameraFragment()
    }
}