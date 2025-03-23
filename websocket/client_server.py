import asyncio
import websockets
import json
from datetime import datetime

# Configurarea WebSocket URL (asigură-te că URL-ul este corect și accesibil din rețea)
uri = "ws://192.168.0.25:8080/ws/sensors"

# Datele senzorului
sensor_data = {
    # "id": 6,
    # "timestamp": datetime.utcnow().isoformat(),
    "temperature": 22.5,
    "humidity": 60.2,
    "light1": 150.5,
    "light2": 200.3,
    "light3": 180.4,
    "voltage1": 12.3,
    "voltage2": 3.4,
    "current1": 1.2,
    "current2": 0.8
}

async def send_sensor_data():
    try:
        # Conectare la server WebSocket
        async with websockets.connect(uri) as websocket:
            # Trimiterea datelor de senzor
            await websocket.send(json.dumps(sensor_data))
            print("Data sent:", json.dumps(sensor_data))

            # Așteptăm un răspuns de la server
            response = await websocket.recv()
            print("Response from server:", response)

    except Exception as e:
        print(f"Error: {e}")

# Rularea scriptului Python
if __name__ == "__main__":
    asyncio.run(send_sensor_data())