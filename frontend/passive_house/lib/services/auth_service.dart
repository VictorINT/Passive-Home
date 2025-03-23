import 'package:google_sign_in/google_sign_in.dart';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import 'package:http/http.dart' as http;
import 'dart:convert';

class AuthService {
  final GoogleSignIn _googleSignIn = GoogleSignIn(
    scopes: [
      'email',
      'profile',
    ],
  );
  
  final FlutterSecureStorage _storage = const FlutterSecureStorage();
  static const String baseUrl = 'http://localhost:8080';
  
  // Sign in with Google
  Future<String?> signInWithGoogle() async {
    try {
      final GoogleSignInAccount? googleUser = await _googleSignIn.signIn();
      if (googleUser == null) return null;
      
      final GoogleSignInAuthentication googleAuth = await googleUser.authentication;
      
      // Exchange Google ID token for our backend token
      final response = await http.post(
        Uri.parse('$baseUrl/auth/google'),
        headers: {'Content-Type': 'application/json'},
        body: json.encode({
          'idToken': googleAuth.idToken,
        }),
      );
      
      if (response.statusCode == 200) {
        final Map<String, dynamic> data = json.decode(response.body);
        final String accessToken = data['accessToken'];
        
        // Store the token
        await _storage.write(key: 'access_token', value: accessToken);
        return accessToken;
      } else {
        throw Exception('Failed to authenticate with backend: ${response.body}');
      }
    } catch (e) {
      print('Error signing in with Google: $e');
      return null;
    }
  }
  
  // Get stored token
  Future<String?> getAccessToken() async {
    return await _storage.read(key: 'access_token');
  }
  
  // Check if user is logged in
  Future<bool> isLoggedIn() async {
    final token = await getAccessToken();
    return token != null;
  }
  
  // Sign out
  Future<void> signOut() async {
    await _googleSignIn.signOut();
    await _storage.delete(key: 'access_token');
  }
}