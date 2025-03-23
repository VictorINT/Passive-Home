import 'dart:convert';
import 'package:http/http.dart' as http;
import '../models/sensor_data.dart';
import '../services/auth_service.dart';

class SensorService {
  static const String baseUrl = 'http://localhost:8080';
  final AuthService _authService = AuthService();

  Future<SensorData> getSensorData() async {
    try {
      final token = await _authService.getAccessToken();
      final response = await http.get(
        Uri.parse('$baseUrl/sensors/last'),
        headers: {
          'Authorization': 'Bearer $token',
        },
      );
      
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
  
  Future<List<SensorData>> getAllSensorData() async {
    try {
      final token = await _authService.getAccessToken();
      final response = await http.get(
        Uri.parse('$baseUrl/sensors/all'),
        headers: {
          'Authorization': 'Bearer $token',
        },
      );
      
      if (response.statusCode == 200) {
        final List<dynamic> jsonData = json.decode(response.body);
        return jsonData.map((data) => SensorData.fromJson(data)).toList();
      } else {
        throw Exception('Failed to load all sensor data');
      }
    } catch (e) {
      throw Exception('Error fetching all sensor data: $e');
    }
  }
}