import asyncio
import websockets
import json
import serial
import time
from datetime import datetime

uri = "ws://192.168.0.25:8080/ws/sensors"

ser = serial.Serial('/dev/ttyACM0', 38400, timeout=1)

time.sleep(3)

def read_temperature_from_serial():
    if ser.in_waiting > 0:
        data = ser.readline().decode('utf-8').strip()
    print("data read: ", data)
    return data

sensor_data = {
    "temperature": 0.0,
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
        async with websockets.connect(uri) as websocket:
            temperature = read_temperature_from_serial()
            if temperature is not None:
                sensor_data["temperature"] = temperature
                await websocket.send(json.dumps(sensor_data))
                print("Data sent:", sensor_data["temperature"])

                response = await websocket.recv()
                print("Response from server:", response)
            else:
                print("Error! Reading from serial failed!.")

    except Exception as e:
        print(f"Error: {e}")


if __name__ == "__main__":
    while True:
        asyncio.run(send_sensor_data())

        time.sleep(0.5)