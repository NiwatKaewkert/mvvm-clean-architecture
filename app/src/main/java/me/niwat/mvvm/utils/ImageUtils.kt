package me.niwat.mvvm.utils

import android.graphics.*
import android.media.Image
import com.google.mlkit.vision.common.InputImage
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer
import kotlin.math.min


fun InputImage.toBitmap(): Bitmap? {
    val planes: Array<Image.Plane> = this.planes as Array<Image.Plane>
    val yBuffer: ByteBuffer = planes[0].buffer
    val uBuffer: ByteBuffer = planes[1].buffer
    val vBuffer: ByteBuffer = planes[2].buffer
    val ySize: Int = yBuffer.remaining()
    val uSize: Int = uBuffer.remaining()
    val vSize: Int = vBuffer.remaining()
    val nv21 = ByteArray(ySize + uSize + vSize)
    //U and V are swapped
    yBuffer.get(nv21, 0, ySize)
    vBuffer.get(nv21, ySize, vSize)
    uBuffer.get(nv21, ySize + vSize, uSize)
    val yuvImage = YuvImage(nv21, ImageFormat.NV21, this.width, this.height, null)
    val out = ByteArrayOutputStream()
    yuvImage.compressToJpeg(Rect(0, 0, yuvImage.width, yuvImage.height), 75, out)
    val imageBytes: ByteArray = out.toByteArray()
    return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
}

fun Bitmap.toCircularBitmap(): Bitmap {
    val width = this.width
    val height = this.height
    val outputBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val path = Path()
    path.addOval(
        width * .28f, height * .22f, width * .90f, height * .80f, Path.Direction.CCW
    )
    val canvas = Canvas(outputBitmap)
    canvas.clipPath(path)
    canvas.drawBitmap(this, 0f, 0f, null)
    return outputBitmap
}