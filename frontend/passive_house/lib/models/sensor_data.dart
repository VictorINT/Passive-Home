class SensorData {
  final double temperature;
  final double humidity;
  final double light1;
  final double light2;
  final double light3;
  final double voltage1;
  final double voltage2;
  final double current1;
  final double current2;

  SensorData({
    required this.temperature,
    required this.humidity,
    required this.light1,
    required this.light2,
    required this.light3,
    required this.voltage1,
    required this.voltage2,
    required this.current1,
    required this.current2,
  });

  factory SensorData.fromJson(Map<String, dynamic> json) {
    return SensorData(
      temperature: json['temperature'].toDouble(),
      humidity: json['humidity'].toDouble(),
      light1: json['light1'].toDouble(),
      light2: json['light2'].toDouble(),
      light3: json['light3'].toDouble(),
      voltage1: json['voltage1'].toDouble(),
      voltage2: json['voltage2'].toDouble(),
      current1: json['current1'].toDouble(),
      current2: json['current2'].toDouble(),
    );
  }
} 