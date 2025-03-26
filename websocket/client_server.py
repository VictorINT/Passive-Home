import asyncio
import websockets
import serial
import time
import threading
import json

# Serial port configuration
ser = serial.Serial('/dev/ttyACM0', 38400, timeout=2)
time.sleep(3)

# WebSocket server URI
uri = "ws://192.168.0.25:8080/ws/sensors"

# Global variables
valtemp = 0.0
lock = threading.Lock()

def read_temperature_from_serial():
    """Thread function to read temperature from serial port"""
    global valtemp
    while True:
        try:
            # Read data from serial port
            if ser.in_waiting > 0:
                data = ser.readline().decode('utf-8').strip()

                # Try to convert to float
                try:
                    temp = float(data)
                    with lock:
                        valtemp = temp
                    print(f"Read temperature: {valtemp}")
                except ValueError:
                    print(f"Invalid temperature data: {data}")

        except Exception as e:
            print(f"Serial read error: {e}")

        time.sleep(0.1)

async def send_temperature_data():
    """Async function to send temperature data via WebSocket"""
    global valtemp
    while True:
        try:
            async with websockets.connect(uri) as websocket:
                with lock:
                    # Send only the temperature value as a JSON payload
                    payload = json.dumps({"temperature": valtemp})

                await websocket.send(payload)
                print(f"Sent temperature: {valtemp}")

                # Optional: receive server response
                response = await websocket.recv()
                print("Server response:", response)

        except Exception as e:
            print(f"WebSocket error: {e}")

        await asyncio.sleep(1)  # Wait for 1 second between sends

async def main():
    # Start serial reading thread
    serial_thread = threading.Thread(target=read_temperature_from_serial, daemon=True)
    serial_thread.start()

    # Start WebSocket sending coroutine
    await send_temperature_data()

if __name__ == "__main__":
    try:
        asyncio.run(main())
    except KeyboardInterrupt:
        print("Stopping...")
    finally:
        ser.close()