package github.swissonid.mlkitobjectdetection

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import io.flutter.embedding.android.FlutterActivity

import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import io.flutter.plugin.common.PluginRegistry
import io.flutter.plugin.common.PluginRegistry.Registrar
private const val VIEW_TYPE = "plugins.github.swissonid/ml_view"
const val ML_VIEW = "plugins.github.swissonid/ml_view"
/** MlkitobjectdetectionPlugin */

private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
private const val REQUEST_CODE_PERMISSIONS = 10
public class MlkitobjectdetectionPlugin: FlutterPlugin, ActivityAware, PluginRegistry.RequestPermissionsResultListener {
  /// The MethodChannel that will the communication between Flutter and native Android
  ///
  /// This local reference serves to register the plugin with the Flutter Engine and unregister it
  /// when the Flutter Engine is detached from the Activity
  private lateinit var channel : MethodChannel
  private var activity: FlutterActivity? = null
  private lateinit var pluginBinding: FlutterPlugin.FlutterPluginBinding
  override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
    pluginBinding = flutterPluginBinding
  }

  // This static function is optional and equivalent to onAttachedToEngine. It supports the old
  // pre-Flutter-1.12 Android projects. You are encouraged to continue supporting
  // plugin registration via this function while apps migrate to use the new Android APIs
  // post-flutter-1.12 via https://flutter.dev/go/android-project-migration.
  //
  // It is encouraged to share logic between onAttachedToEngine and registerWith to keep
  // them functionally equivalent. Only one of onAttachedToEngine or registerWith will be called
  // depending on the user's project. onAttachedToEngine or registerWith must both be defined
  // in the same class.
  companion object {
    @JvmStatic
    fun registerWith(registrar: Registrar) {
      val activity = registrar.activity() as FlutterActivity
      val messenger = registrar.messenger()
      val cameraViewFactory = CameraViewFactory(messenger, activity)
      registrar.platformViewRegistry().registerViewFactory(VIEW_TYPE, cameraViewFactory)

    }
  }

  override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
    channel.setMethodCallHandler(null)
  }

  override fun onDetachedFromActivity() {
    activity = null
  }

  override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
    binding.activity as FlutterActivity
    binding.addRequestPermissionsResultListener(this)
  }

  override fun onAttachedToActivity(binding: ActivityPluginBinding) {
    val messenger = pluginBinding.binaryMessenger
    //val viewFactory = TextViewFactory(messenger)
    val activity = binding.activity as FlutterActivity
    val viewFactory = CameraViewFactory(messenger, activity)
    pluginBinding.platformViewRegistry.registerViewFactory(VIEW_TYPE,viewFactory)
    //
  }

  override fun onDetachedFromActivityForConfigChanges() {}
  override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>?, grantResults: IntArray?): Boolean {
    TODO("Not yet implemented")
  }

}
