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
    ImageAnalysis.Analyzer, SurfaceHolder.Callback {

    override val viewModel: CameraFragmentViewModel by activityViewModel()
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var holder: SurfaceHolder? = null
    var radius: Float = 0f
    var centerX: Float = 0f
    var centerY: Float = 0f

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
        viewModel.isDetected.observe(this) {
            val color = if (it) Color.GREEN else Color.RED
            changeColor(color)
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

    override fun surfaceCreated(holder: SurfaceHolder) {
        this.holder = holder
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        this.holder = holder
        drawFocusRect(Color.RED)
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        this.holder = null
    }

    private fun drawFocusRect(color: Int) {
        binding.apply {
            val height: Int = cameraView.height
            val width: Int = cameraView.width

            val canvas = holder?.lockCanvas()
            val outerRectangle = RectF(0f, 0f, width.toFloat(), height.toFloat())

            //draw background
            paint.color = Color.BLACK
            paint.alpha = 100
            canvas?.drawRect(outerRectangle, paint)

            //draw circle
            paint.color = Color.TRANSPARENT
            paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_OUT)
            radius = (min(
                width,
                height
            ) / 2 - 100).toFloat()
            centerX = (width / 2).toFloat()
            centerY = (height / 3).toFloat()

            canvas?.drawCircle(centerX, centerY, radius, paint)

            //draw stroke
            paint.style = Paint.Style.STROKE
            paint.color = color
            paint.strokeWidth = 10f
            canvas?.drawCircle(centerX, centerY, radius, paint)

            holder?.unlockCanvasAndPost(canvas)
        }
    }

    private fun changeColor(color: Int) {
        binding.apply {
            val canvas = holder?.lockCanvas()
            //draw stroke
            paint.style = Paint.Style.STROKE
            paint.color = color
            paint.strokeWidth = 10f
            canvas?.drawCircle(centerX, centerY, radius, paint)
            holder?.unlockCanvasAndPost(canvas)
        }
    }

    companion object {
        private var TAG = CameraFragment::class.java.simpleName
        fun newInstance(): Fragment = CameraFragment()
    }
}