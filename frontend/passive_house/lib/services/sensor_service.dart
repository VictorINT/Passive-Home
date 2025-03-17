import 'dart:convert';
import 'package:http/http.dart' as http;
import '../models/sensor_data.dart';

class SensorService {
  static const String baseUrl = 'http://localhost:8080';

  Future<SensorData> getSensorData() async {
    try {
      final response = await http.get(Uri.parse('$baseUrl/sensors/last'));
      if (response.statusCode == 200) {
        final Map<String, dynamic> jsonData = json.decode(response.body);
        return SensorData.fromJson(jsonData);
      } else {
        throw Exception('Failed to load sensor data');
      }
    } catch (e) {
      throw Exception('Error fetching sensor data: $e');
    }
  }
} 