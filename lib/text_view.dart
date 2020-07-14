import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

typedef void TextViewCreatedCallback(TextViewController controller);

class TextView extends StatefulWidget {
  final TextViewCreatedCallback onTextViewCreated;
  const TextView({Key key, this.onTextViewCreated}) : super(key: key);

  @override
  _TextViewState createState() => _TextViewState();
}

class _TextViewState extends State<TextView> {
  @override
  Widget build(BuildContext context) {
    if (defaultTargetPlatform == TargetPlatform.android) {
      return IgnorePointer(
        child: AndroidView(
          viewType: 'plugins.github.swissonid/ml_view',
          onPlatformViewCreated: _onPlatformViewCreated,
        ),
      );
    }
    return Center(
      child: Text('This feature is on your platform currently unsupported'),
    );
  }

  void _onPlatformViewCreated(int id) {
    if (widget.onTextViewCreated == null) {
      return;
    }
    widget.onTextViewCreated(TextViewController._(id));
  }
}

class TextViewController {
  TextViewController._(int id)
      : _channel = MethodChannel('plugins.github.swissonid/ml_view_$id');

  final MethodChannel _channel;

  Future<void> setText(String text) {
    assert(text != null);
    return _channel.invokeMethod('setText', text);
  }
}
