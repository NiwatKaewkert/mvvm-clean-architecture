package me.niwat.mvvm.presenter.camera

import android.graphics.Color
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.PorterDuff
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
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import java.util.concurrent.Executor


class CameraFragment :
    BaseFragment<FragmentCameraBinding, CameraFragmentViewModel>(FragmentCameraBinding::inflate),
    ImageAnalysis.Analyzer, SurfaceHolder.Callback {

    var xOffset: Int = 0
    var yOffset: Int = 0
    var boxWidth: Int = 0
    var boxHeight: Int = 0

    override val viewModel: CameraFragmentViewModel by activityViewModel()

    override fun init() {
    }

    override fun updateUI(view: View, savedInstanceState: Bundle?) {
        binding.apply {
            overlay.setZOrderOnTop(true)
            overlay.holder.setFormat(PixelFormat.TRANSLUCENT)
            overlay.holder.addCallback(this@CameraFragment)
        }
        startCamera()
    }

    override fun observer() {
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


    companion object {
        private var TAG = CameraFragment::class.java.simpleName
        fun newInstance(): Fragment = CameraFragment()
    }

    @ExperimentalGetImage
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage: Image? = imageProxy.image
        mediaImage?.let {
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
            viewModel.detectFaces(image, imageProxy)
        }
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        //Drawing rectangle
        drawFocusRect(Color.parseColor("#b3dabb"))
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
    }

    private fun drawFocusRect(color: Int) {
        binding.apply {
            val height: Int = cameraView.height
            val width: Int = cameraView.width

            val left: Int
            val right: Int
            val top: Int
            val bottom: Int
            var diameter: Int
            diameter = width
            if (height < width) {
                diameter = height
            }
            val offset = (0.05 * diameter).toInt()
            diameter -= offset
            val canvas = overlay.holder.lockCanvas()
            canvas.drawColor(0, PorterDuff.Mode.CLEAR)
            //border's properties
            val paint = Paint()
            paint.style = Paint.Style.STROKE
            paint.color = color
            paint.strokeWidth = 5f
            left = width / 2 - diameter / 3
            top = height / 2 - diameter / 3
            right = width / 2 + diameter / 3
            bottom = height / 2 + diameter / 3
            xOffset = left
            yOffset = top
            boxHeight = bottom - top
            boxWidth = right - left
            //Changing the value of x in diameter/x will change the size of the box ; inversely proportionate to x
            canvas.drawRect(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat(), paint)
            overlay.holder.unlockCanvasAndPost(canvas)
        }
    }
}