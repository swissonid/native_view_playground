package github.swissonid.mlkitobjectdetection

import android.content.Context
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import io.flutter.plugin.common.BinaryMessenger
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.platform.PlatformView

class FlutterTextView(private val context: Context, messenger: BinaryMessenger, viewId: Int) : PlatformView, MethodChannel.MethodCallHandler {
    private val textView = TextView(context)
    private val methodChannel = MethodChannel(messenger, "${ML_VIEW}_${viewId}")
    override fun getView(): View = textView
    override fun dispose() { /* do nothing */ }

    init {
        methodChannel.setMethodCallHandler(this)
    }

    override fun onMethodCall(call: MethodCall, result: MethodChannel.Result) {
        when(call.method) {
            "setText" -> setText(call, result)
            else -> result.notImplemented()
        }
    }

    private fun setText(call: MethodCall, result: MethodChannel.Result) {
        val text = call.arguments as String
        textView.text = text
        result.success(null)
    }

}
