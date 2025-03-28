import asyncio
import websockets
import serial
import time
import threading
import json

# Configurare port serial
ser = serial.Serial('/dev/ttyACM0', 38400, timeout=1)
time.sleep(1)

# WebSocket server URI
uri = "ws://192.168.0.25:8080/ws/sensors"

# Variabilă partajată pentru citirea senzorului
latest_temperature = None

def read_temperature():
    """Thread pentru citirea continuă de la serial"""
    global latest_temperature
    while True:
        try:
            if ser.in_waiting > 0:
                data = ser.readline().decode('utf-8').strip()
                try:
                    temp = float(data)
                    latest_temperature = temp  # Actualizare valoare
                    print(f"[SERIAL] Citit: {temp}")  # Debug
                except ValueError:
                    print(f"[SERIAL] Date invalide: {data}")
        except Exception as e:
            print(f"[ERROR] Citire serial: {e}")

def send_temperature():
    """Thread pentru trimiterea datelor la fiecare 30 secunde"""
    global latest_temperature
    while True:
        try:
            time.sleep(30)  # Așteaptă 30 secunde
            if latest_temperature is not None:
                payload = json.dumps({"temperature": latest_temperature})
                asyncio.run(send_to_websocket(payload))
        except Exception as e:
            print(f"[ERROR] Trimitere WebSocket: {e}")

async def send_to_websocket(payload):
    """Trimite un mesaj WebSocket"""
    try:
        async with websockets.connect(uri) as websocket:
            await websocket.send(payload)
            print(f"[WS] Trimis: {payload}")
    except Exception as e:
        print(f"[WS ERROR] Conexiune WebSocket: {e}")

async def receive_websocket():
    """Ascultă WebSocket și trimite comenzi pe serial"""
    while True:
        try:
            async with websockets.connect(uri) as websocket:
                async for message in websocket:
                    try:
                        ser.write((message + "\n").encode('utf-8'))
                        print(f"[WS] Primit și trimis pe serial: {message}")
                    except Exception as e:
                        print(f"[ERROR] Scriere serial: {e}")
        except Exception as e:
            print(f"[WS ERROR] Conexiune WebSocket pierdută: {e}")
            await asyncio.sleep(5) 

# Pornire threaduri
threading.Thread(target=read_temperature, daemon=True).start()
threading.Thread(target=send_temperature, daemon=True).start()
asyncio.run(receive_websocket())  # Start WebSocket listener
