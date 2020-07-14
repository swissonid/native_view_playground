#import "MlkitobjectdetectionPlugin.h"
#if __has_include(<mlkitobjectdetection/mlkitobjectdetection-Swift.h>)
#import <mlkitobjectdetection/mlkitobjectdetection-Swift.h>
#else
// Support project import fallback if the generated compatibility header
// is not copied when this plugin is created as a library.
// https://forums.swift.org/t/swift-static-libraries-dont-copy-generated-objective-c-header/19816
#import "mlkitobjectdetection-Swift.h"
#endif

@implementation MlkitobjectdetectionPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftMlkitobjectdetectionPlugin registerWithRegistrar:registrar];
}
@end
