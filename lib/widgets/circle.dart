import 'package:flutter/material.dart';

class Circle extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return Stack(
      alignment: AlignmentDirectional.center,
      children: <Widget>[
        _InnerCircle(),
        _OuterCircle(),
      ],
    );
  }
}

class _Circle extends StatelessWidget {
  final double size;
  final Color color;
  final double borderWith;
  final Border border;

  const _Circle({Key key, this.size, this.color, this.borderWith, this.border})
      : assert(
            (color == null || borderWith == null) || border == null,
            'Cannot provide all three a color, a borderWith and a border\n'
            'To provide both, use "border: Border.all(color: color, with: borderWith)".'),
        super(key: key);
  @override
  Widget build(BuildContext context) {
    return Container(
      height: size,
      decoration: BoxDecoration(
        border: _getBorder(),
        shape: BoxShape.circle,
      ),
    );
  }

  Border _getBorder() {
    if (border != null) {
      return border;
    }
    final internalBorderWith = borderWith ?? 2;
    final internalColor = color ?? Colors.white;
    return Border.all(color: internalColor, width: internalBorderWith);
  }
}

class _InnerCircle extends StatelessWidget {
  final double size;
  const _InnerCircle({Key key, this.size = 32.0})
      : assert(size != null),
        super(key: key);
  @override
  Widget build(BuildContext context) {
    return _Circle(size: size);
  }
}

class _OuterCircle extends StatefulWidget {
  final double size;

  const _OuterCircle({Key key, this.size = 54})
      : assert(size != null),
        super(key: key);
  @override
  State<StatefulWidget> createState() => _OuterCircleState();
}

class _OuterCircleState extends State<_OuterCircle>
    with TickerProviderStateMixin {
  AnimationController _animationController;
  Animation<double> _increaseOpacity;
  Animation<double> _fadeOutOpacity;
  Animation<double> _resetOpacityImmediately;
  Animation<double> _scale;
  @override
  void initState() {
    super.initState();
    _animationController = AnimationController(
      duration: const Duration(milliseconds: 2800),
      vsync: this,
    )..repeat();

    final someTween = Tween(begin: 0.4, end: 1.0);
    _increaseOpacity = someTween.animate(
      CurvedAnimation(
        curve: Interval(0, 0.66, curve: Curves.fastOutSlowIn),
        parent: _animationController,
      ),
    );
    _resetOpacityImmediately = ReverseTween(someTween).animate(
      CurvedAnimation(
        curve: Interval(0.66, 0.66, curve: Curves.easeOutQuad),
        parent: _animationController,
      ),
    );
    _scale = Tween(begin: 1.0, end: 1.5).animate(
      CurvedAnimation(
        curve: Interval(0.65, 1.0, curve: Curves.easeOutCirc),
        parent: _animationController,
      ),
    );

    _fadeOutOpacity = Tween(begin: 1.0, end: 0.0).animate(
      CurvedAnimation(
        curve: Interval(0.85, 1.0, curve: Curves.easeOutQuad),
        parent: _animationController,
      ),
    );
  }

  @override
  void dispose() {
    _animationController.dispose();
    super.dispose();
  }

  double _thinOutBorder(double currentWith) {
    var borderWith = (4 - (currentWith * 4.4));
    if (borderWith < 0) {
      borderWith = 0;
    }
    return borderWith;
  }

  @override
  Widget build(BuildContext context) {
    return Stack(
      alignment: AlignmentDirectional.center,
      children: <Widget>[
        _InnerCircle(),
        ScaleTransition(
          scale: _scale,
          child: FadeTransition(
            opacity: _fadeOutOpacity,
            child: AnimatedBuilder(
              animation: _animationController,
              builder: (context, _) {
                return _Circle(
                  size: widget.size,
                  borderWith: _thinOutBorder(_animationController.value),
                );
              },
            ),
          ),
        ),
        _Circle(
          size: widget.size,
          borderWith: 4,
          color: Colors.grey.withOpacity(0.8),
        ),
        FadeTransition(
          opacity: _increaseOpacity,
          child: FadeTransition(
            opacity: _resetOpacityImmediately,
            child: _Circle(
              size: widget.size,
              borderWith: 4,
            ),
          ),
        ),
      ],
    );
  }
}
