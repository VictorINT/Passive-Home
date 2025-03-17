import 'package:flutter/material.dart';
import '../models/sensor_data.dart';
import '../services/sensor_service.dart';

class SensorDetailPage extends StatefulWidget {
  const SensorDetailPage({super.key});

  @override
  State<SensorDetailPage> createState() => _SensorDetailPageState();
}

class _SensorDetailPageState extends State<SensorDetailPage> {
  final SensorService _sensorService = SensorService();
  SensorData? _sensorData;
  bool _isLoading = true;
  String? _error;

  @override
  void initState() {
    super.initState();
    _loadSensorData();
  }

  Future<void> _loadSensorData() async {
    try {
      final data = await _sensorService.getSensorData();
      setState(() {
        _sensorData = data;
        _isLoading = false;
      });
    } catch (e) {
      setState(() {
        _error = e.toString();
        _isLoading = false;
      });
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Sensor Details'),
        backgroundColor: Theme.of(context).colorScheme.inversePrimary,
      ),
      body: _isLoading
          ? const Center(child: CircularProgressIndicator())
          : _error != null
              ? Center(child: Text('Error: $_error'))
              : _sensorData == null
                  ? const Center(child: Text('No data available'))
                  : SingleChildScrollView(
                      padding: const EdgeInsets.all(16.0),
                      child: Column(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        children: [
                          _buildSensorCard(
                            'Temperature',
                            '${_sensorData!.temperature}°C',
                            Icons.thermostat,
                          ),
                          const SizedBox(height: 16),
                          _buildSensorCard(
                            'Humidity',
                            '${_sensorData!.humidity}%',
                            Icons.water_drop,
                          ),
                          const SizedBox(height: 16),
                          _buildSensorCard(
                            'Light Sensors',
                            'L1: ${_sensorData!.light1}%\nL2: ${_sensorData!.light2}%\nL3: ${_sensorData!.light3}%',
                            Icons.lightbulb,
                          ),
                          const SizedBox(height: 16),
                          _buildSensorCard(
                            'Voltage',
                            'V1: ${_sensorData!.voltage1}V\nV2: ${_sensorData!.voltage2}V',
                            Icons.electric_bolt,
                          ),
                          const SizedBox(height: 16),
                          _buildSensorCard(
                            'Current',
                            'I1: ${_sensorData!.current1}A\nI2: ${_sensorData!.current2}A',
                            Icons.electrical_services,
                          ),
                        ],
                      ),
                    ),
    );
  }

  Widget _buildSensorCard(String title, String value, IconData icon) {
    return Card(
      elevation: 4,
      child: Padding(
        padding: const EdgeInsets.all(16.0),
        child: Row(
          children: [
            Icon(icon, size: 40, color: Theme.of(context).primaryColor),
            const SizedBox(width: 16),
            Expanded(
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text(
                    title,
                    style: Theme.of(context).textTheme.titleLarge,
                  ),
                  const SizedBox(height: 8),
                  Text(
                    value,
                    style: Theme.of(context).textTheme.bodyLarge,
                  ),
                ],
              ),
            ),
          ],
        ),
      ),
    );
  }
} 