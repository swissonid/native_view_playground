package github.swissonid.mlkitobjectdetection

import android.app.Activity
import android.content.Context
import android.content.Context.CAMERA_SERVICE
import android.graphics.Bitmap
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.media.Image
import android.net.Uri
import android.os.Build
import android.util.SparseIntArray
import android.view.Surface
import androidx.annotation.RequiresApi
import com.google.mlkit.vision.common.InputImage
import java.io.IOException
import java.nio.ByteBuffer

class MLKitVisionImage {

    private fun imageFromBitmap(bitmap: Bitmap) {
        val rotationDegrees = 0
        // [START image_from_bitmap]
        val image = InputImage.fromBitmap(bitmap, 0)
        // [END image_from_bitmap]
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private fun imageFromMediaImage(mediaImage: Image, rotation: Int) {
        // [START image_from_media_image]
        val image = InputImage.fromMediaImage(mediaImage, rotation)
        // [END image_from_media_image]
    }

    private fun imageFromBuffer(byteBuffer: ByteBuffer, rotationDegrees: Int) {
        // [START set_metadata]
        // TODO How do we document the FrameMetadata developers need to implement?
        // [END set_metadata]
        // [START image_from_buffer]
        val image = InputImage.fromByteBuffer(
                byteBuffer,
                /* image width */ 480,
                /* image height */ 360,
                rotationDegrees,
                InputImage.IMAGE_FORMAT_NV21 // or IMAGE_FORMAT_YV12
        )
        // [END image_from_buffer]
    }

    private fun imageFromArray(byteArray: ByteArray, rotationDegrees: Int) {
        // [START image_from_array]
        val image = InputImage.fromByteArray(
                byteArray,
                /* image width */ 480,
                /* image height */ 360,
                rotationDegrees,
                InputImage.IMAGE_FORMAT_NV21 // or IMAGE_FORMAT_YV12
        )

        // [END image_from_array]
    }

    private fun imageFromPath(context: Context, uri: Uri) {
        // [START image_from_path]
        val image: InputImage
        try {
            image = InputImage.fromFilePath(context, uri)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        // [END image_from_path]
    }

    // [START get_rotation]
    /**
     * Get the angle by which an image must be rotated given the device's current
     * orientation.
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Throws(CameraAccessException::class)
    private fun getRotationCompensation(cameraId: String, activity: Activity, context: Context): Int {
        // Get the device's current rotation relative to its "native" orientation.
        // Then, from the ORIENTATIONS table, look up the angle the image must be
        // rotated to compensate for the device's rotation.
        val deviceRotation = activity.windowManager.defaultDisplay.rotation
        var rotationCompensation = ORIENTATIONS.get(deviceRotation)

        // On most devices, the sensor orientation is 90 degrees, but for some
        // devices it is 270 degrees. For devices with a sensor orientation of
        // 270, rotate the image an additional 180 ((270 + 270) % 360) degrees.
        val cameraManager = context.getSystemService(CAMERA_SERVICE) as CameraManager
        val sensorOrientation = cameraManager
                .getCameraCharacteristics(cameraId)
                .get(CameraCharacteristics.SENSOR_ORIENTATION)!!
        rotationCompensation = (rotationCompensation + sensorOrientation + 270) % 360

        return rotationCompensation
    }
    // [END get_rotation]

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Throws(CameraAccessException::class)
    private fun getCompensation(activity: Activity, context: Context) {
        // Get the ID of the camera using CameraManager. Then:
        val rotation = getRotationCompensation(MY_CAMERA_ID, activity, context)
    }

    companion object {

        private val TAG = "MLKIT"
        private val MY_CAMERA_ID = "my_camera_id"

        // [START camera_orientations]
        private val ORIENTATIONS = SparseIntArray()

        init {
            ORIENTATIONS.append(Surface.ROTATION_0, 90)
            ORIENTATIONS.append(Surface.ROTATION_90, 0)
            ORIENTATIONS.append(Surface.ROTATION_180, 270)
            ORIENTATIONS.append(Surface.ROTATION_270, 180)
        }
        // [END camera_orientations]
    }
}