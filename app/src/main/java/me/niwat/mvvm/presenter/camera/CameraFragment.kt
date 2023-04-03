package me.niwat.mvvm.presenter.camera

import android.graphics.*
import android.media.Image
import android.os.Bundle
import android.util.Log
import android.view.SurfaceHolder
import android.view.View
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.mlkit.vision.common.InputImage
import me.niwat.mvvm.base.BaseFragment
import me.niwat.mvvm.databinding.FragmentCameraBinding
import me.niwat.mvvm.utils.toBitmap
import me.niwat.mvvm.utils.toCircularBitmap
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import java.util.concurrent.Executor
import kotlin.math.min


class CameraFragment :
    BaseFragment<FragmentCameraBinding, CameraFragmentViewModel>(FragmentCameraBinding::inflate),
    ImageAnalysis.Analyzer {

    override val viewModel: CameraFragmentViewModel by activityViewModel()
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var holder: SurfaceHolder? = null
    var radius: Float = 0f
    var centerX: Float = 0f
    var centerY: Float = 0f

    override fun init() {
    }

    override fun updateUI(view: View, savedInstanceState: Bundle?) {
        startCamera()
    }

    override fun observer() {
        viewModel.isDetected.observe(this) {
            val color = if (it) Color.GREEN else Color.RED
            binding.circleBorder.changeBorderColor(color)
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

    @ExperimentalGetImage
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage: Image? = imageProxy.image
        mediaImage?.let {
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
            val bmp = image.toBitmap()

            bmp?.let {
                val imageBitmap = bmp.toCircularBitmap()
                viewModel.detectFaces(imageBitmap, imageProxy)
            }
        }
    }

    companion object {
        private var TAG = CameraFragment::class.java.simpleName
        fun newInstance(): Fragment = CameraFragment()
    }
}