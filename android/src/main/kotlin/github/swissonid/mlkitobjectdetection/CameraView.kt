package github.swissonid.mlkitobjectdetection

import android.content.Context
import android.util.Log
import android.util.Size
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import io.flutter.plugin.common.BinaryMessenger
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.platform.PlatformView
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CameraView (private val context: Context,
                  private val lifecycleOwner: LifecycleOwner,
                  messenger: BinaryMessenger,
                  viewId: Int) :PlatformView, MethodChannel.MethodCallHandler  {
    private val methodChannel = MethodChannel(messenger, "${ML_VIEW}_${viewId}")
    private var camera: Camera? = null
    private var preview: Preview? = null
    private var imageAnalyzer: ImageAnalysis? = null
    private var viewFinder = PreviewView(context)
    /** Blocking camera operations are performed using this executor */
    private var cameraExecutor: ExecutorService = Executors.newSingleThreadExecutor()
        private val objectDetectionAnalyzer = ObjectDetectionAnalyzer()
    init {
        viewFinder.layoutParams = LinearLayout.LayoutParams(
               LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT)
        methodChannel.setMethodCallHandler(this)

    }
    override fun getView(): View {

        startCamera()
        return viewFinder
    }


    override fun dispose() {}
    
    override fun onMethodCall(call: MethodCall, result: MethodChannel.Result) {
        when(call.method) {
            "setText" -> setText(call, result)
            else -> result.notImplemented()
        }
    }

    private fun setText(call: MethodCall, result: MethodChannel.Result) {
        val text = call.arguments as String
        Toast.makeText(context,text,Toast.LENGTH_SHORT).show()
        result.success(null)
    }


    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        cameraProviderFuture.addListener(Runnable {
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Preview
            preview = Preview.Builder().build()
            val imageAnalysis = ImageAnalysis.Builder()
                    .setTargetResolution(Size(1280, 720))
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()
            imageAnalysis.setAnalyzer(cameraExecutor, ImageAnalysis.Analyzer {image ->
                val rotationDegrees = image.imageInfo.rotationDegrees
            })
            // Select back camera
            val cameraSelector =  CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build()

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                val camera = cameraProvider.bindToLifecycle(
                        lifecycleOwner, cameraSelector, preview)
                preview?.setSurfaceProvider(viewFinder.createSurfaceProvider())
            } catch(exc: Exception) {
                Log.e("FlutterTextView", "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(context))
    }
}