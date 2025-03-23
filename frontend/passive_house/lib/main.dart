import 'package:flutter/material.dart';
import 'dart:async'; // Add Timer import
import 'pages/sensor_detail_page.dart';
import 'pages/sensor_graph_page.dart';
import 'pages/login_page.dart';
import 'services/auth_service.dart';
import 'services/sensor_service.dart'; // Add SensorService import
import 'models/sensor_data.dart'; // Add SensorData import

void main() {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Passive House Monitor',
      theme: ThemeData(
        colorScheme: ColorScheme.fromSeed(seedColor: Colors.green),
        useMaterial3: true,
      ),
      initialRoute: '/',
      routes: {
        '/': (context) => const AuthWrapper(),
        '/home': (context) => const MyHomePage(title: 'Passive House Monitor'),
        '/details': (context) => const SensorDetailPage(),
        '/graphs': (context) => const SensorGraphPage(),
      },
    );
  }
}

class AuthWrapper extends StatefulWidget {
  const AuthWrapper({super.key});

  @override
  State<AuthWrapper> createState() => _AuthWrapperState();
}

class _AuthWrapperState extends State<AuthWrapper> {
  final AuthService _authService = AuthService();
  bool _isLoading = true;
  bool _isAuthenticated = false;

  @override
  void initState() {
    super.initState();
    _checkAuthStatus();
  }

  Future<void> _checkAuthStatus() async {
    final isLoggedIn = await _authService.isLoggedIn();
    setState(() {
      _isAuthenticated = isLoggedIn;
      _isLoading = false;
    });
  }

  @override
  Widget build(BuildContext context) {
    if (_isLoading) {
      return const Scaffold(
        body: Center(
          child: CircularProgressIndicator(),
        ),
      );
    }

    if (_isAuthenticated) {
      return const MyHomePage(title: 'Passive House Monitor');
    } else {
      return const LoginPage();
    }
  }
}

class MyHomePage extends StatefulWidget {
  const MyHomePage({super.key, required this.title});

  final String title;

  @override
  State<MyHomePage> createState() => _MyHomePageState();
}

class _MyHomePageState extends State<MyHomePage> {
  final SensorService _sensorService = SensorService();
  final AuthService _authService = AuthService();
  SensorData? _sensorData;
  bool _isLoading = true;
  String? _error;
  Timer? _refreshTimer;

  @override
  void initState() {
    super.initState();
    _loadSensorData();
    // Refresh data every 5 seconds
    _refreshTimer = Timer.periodic(const Duration(seconds: 5), (timer) {
      _loadSensorData();
    });
  }

  @override
  void dispose() {
    _refreshTimer?.cancel();
    super.dispose();
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

  Future<void> _handleSignOut() async {
    await _authService.signOut();
    if (mounted) {
      Navigator.pushReplacementNamed(context, '/');
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        backgroundColor: Theme.of(context).colorScheme.inversePrimary,
        title: Text(widget.title),
        actions: [
          IconButton(
            icon: const Icon(Icons.logout),
            onPressed: _handleSignOut,
            tooltip: 'Sign Out',
          ),
        ],
      ),
      body: _isLoading
          ? const Center(child: CircularProgressIndicator())
          : _error != null
              ? Center(child: Text('Error: $_error'))
              : _sensorData == null
                  ? const Center(child: Text('No data available'))
                  : GestureDetector(
                      onTap: () {
                        Navigator.pushNamed(context, '/details');
                      },
                      child: Container(
                        margin: const EdgeInsets.all(16.0),
                        padding: const EdgeInsets.all(16.0),
                        decoration: BoxDecoration(
                          color: Colors.white,
                          borderRadius: BorderRadius.circular(12),
                          boxShadow: [
                            BoxShadow(
                              color: Colors.grey.withOpacity(0.2),
                              spreadRadius: 2,
                              blurRadius: 8,
                              offset: const Offset(0, 2),
                            ),
                          ],
                        ),
                        child: Column(
                          children: [
                            Row(
                              mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                              children: [
                                _buildSensorItem(
                                  'Temperature',
                                  '${_sensorData!.temperature}°C',
                                  Icons.thermostat,
                                  Colors.orange,
                                ),
                                _buildDivider(),
                                _buildSensorItem(
                                  'Humidity',
                                  '${_sensorData!.humidity}%',
                                  Icons.water_drop,
                                  Colors.blue,
                                ),
                                _buildDivider(),
                                _buildSensorItem(
                                  'Light',
                                  '${_sensorData!.light1}%',
                                  Icons.lightbulb,
                                  Colors.yellow,
                                ),
                                _buildDivider(),
                                _buildSensorItem(
                                  'Power',
                                  '${_sensorData!.voltage1}V',
                                  Icons.electric_bolt,
                                  Colors.purple,
                                ),
                              ],
                            ),
                            const SizedBox(height: 20),
                            ElevatedButton(
                              onPressed: () {
                                Navigator.pushNamed(context, '/graphs');
                              },
                              child: const Text('View Historical Data'),
                            ),
                          ],
                        ),
                      ),
                    ),
    );
  }

  Widget _buildSensorItem(String title, String value, IconData icon, Color color) {
    return Expanded(
      child: Column(
        mainAxisSize: MainAxisSize.min,
        children: [
          Icon(icon, size: 32, color: color),
          const SizedBox(height: 8),
          Text(
            title,
            style: Theme.of(context).textTheme.titleSmall,
            textAlign: TextAlign.center,
          ),
          const SizedBox(height: 4),
          Text(
            value,
            style: Theme.of(context).textTheme.titleMedium?.copyWith(
              fontWeight: FontWeight.bold,
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildDivider() {
    return Container(
      height: 40,
      width: 1,
      color: Colors.grey.withOpacity(0.3),
    );
  }
}