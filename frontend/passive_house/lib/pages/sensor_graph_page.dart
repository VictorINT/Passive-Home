import 'package:flutter/material.dart';
import 'package:fl_chart/fl_chart.dart';
import '../models/sensor_data.dart';
import '../services/sensor_service.dart';

class SensorGraphPage extends StatefulWidget {
  const SensorGraphPage({super.key});

  @override
  State<SensorGraphPage> createState() => _SensorGraphPageState();
}

class _SensorGraphPageState extends State<SensorGraphPage> {
  final SensorService _sensorService = SensorService();
  List<SensorData>? _sensorDataList;
  bool _isLoading = true;
  String? _error;
  String _selectedMetric = 'temperature';

  @override
  void initState() {
    super.initState();
    _loadAllSensorData();
  }

  Future<void> _loadAllSensorData() async {
    try {
      final data = await _sensorService.getAllSensorData();
      setState(() {
        _sensorDataList = data;
        _isLoading = false;
      });
    } catch (e) {
      setState(() {
        _error = e.toString();
        _isLoading = false;
      });
    }
  }

  List<FlSpot> _getSpots() {
    if (_sensorDataList == null || _sensorDataList!.isEmpty) {
      return [];
    }

    return List.generate(_sensorDataList!.length, (index) {
      final data = _sensorDataList![index];
      final double value = _getValueForMetric(data, _selectedMetric);
      return FlSpot(index.toDouble(), value);
    });
  }

  double _getValueForMetric(SensorData data, String metric) {
    switch (metric) {
      case 'temperature':
        return data.temperature;
      case 'humidity':
        return data.humidity;
      case 'light1':
        return data.light1;
      case 'light2':
        return data.light2;
      case 'light3':
        return data.light3;
      case 'voltage1':
        return data.voltage1;
      case 'voltage2':
        return data.voltage2;
      case 'current1':
        return data.current1;
      case 'current2':
        return data.current2;
      default:
        return 0;
    }
  }

  String _getUnitForMetric(String metric) {
    if (metric == 'temperature') return '°C';
    if (metric == 'humidity' || metric.startsWith('light')) return '%';
    if (metric.startsWith('voltage')) return 'V';
    if (metric.startsWith('current')) return 'A';
    return '';
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Sensor History'),
        backgroundColor: Theme.of(context).colorScheme.inversePrimary,
      ),
      body: _isLoading
          ? const Center(child: CircularProgressIndicator())
          : _error != null
              ? Center(child: Text('Error: $_error'))
              : _sensorDataList == null || _sensorDataList!.isEmpty
                  ? const Center(child: Text('No historical data available'))
                  : Padding(
                      padding: const EdgeInsets.all(16.0),
                      child: Column(
                        children: [
                          DropdownButton<String>(
                            value: _selectedMetric,
                            onChanged: (String? newValue) {
                              if (newValue != null) {
                                setState(() {
                                  _selectedMetric = newValue;
                                });
                              }
                            },
                            items: [
                              DropdownMenuItem(value: 'temperature', child: Text('Temperature (°C)')),
                              DropdownMenuItem(value: 'humidity', child: Text('Humidity (%)')),
                              DropdownMenuItem(value: 'light1', child: Text('Light 1 (%)')),
                              DropdownMenuItem(value: 'light2', child: Text('Light 2 (%)')),
                              DropdownMenuItem(value: 'light3', child: Text('Light 3 (%)')),
                              DropdownMenuItem(value: 'voltage1', child: Text('Voltage 1 (V)')),
                              DropdownMenuItem(value: 'voltage2', child: Text('Voltage 2 (V)')),
                              DropdownMenuItem(value: 'current1', child: Text('Current 1 (A)')),
                              DropdownMenuItem(value: 'current2', child: Text('Current 2 (A)')),
                            ],
                          ),
                          const SizedBox(height: 24),
                          Expanded(
                            child: LineChart(
                              LineChartData(
                                gridData: FlGridData(show: true),
                                titlesData: FlTitlesData(
                                  bottomTitles: AxisTitles(
                                    sideTitles: SideTitles(
                                      showTitles: true,
                                      getTitlesWidget: (value, meta) {
                                        // Simple index display, could be replaced with timestamps
                                        if (value % 5 == 0 && value < _sensorDataList!.length) {
                                          return Text(value.toInt().toString());
                                        }
                                        return const Text('');
                                      },
                                      reservedSize: 22,
                                    ),
                                  ),
                                  leftTitles: AxisTitles(
                                    sideTitles: SideTitles(
                                      showTitles: true,
                                      getTitlesWidget: (value, meta) {
                                        return Text('${value.toStringAsFixed(1)}${_getUnitForMetric(_selectedMetric)}');
                                      },
                                      reservedSize: 40,
                                    ),
                                  ),
                                  topTitles: AxisTitles(sideTitles: SideTitles(showTitles: false)),
                                  rightTitles: AxisTitles(sideTitles: SideTitles(showTitles: false)),
                                ),
                                borderData: FlBorderData(show: true),
                                lineBarsData: [
                                  LineChartBarData(
                                    spots: _getSpots(),
                                    isCurved: true,
                                    color: Theme.of(context).primaryColor,
                                    barWidth: 3,
                                    dotData: FlDotData(show: false),
                                    belowBarData: BarAreaData(
                                      show: true,
                                      color: Theme.of(context).primaryColor.withOpacity(0.2),
                                    ),
                                  ),
                                ],
                              ),
                            ),
                          ),
                        ],
                      ),
                    ),
    );
  }
}