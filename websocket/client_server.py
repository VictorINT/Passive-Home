import asyncio
import websockets
import serial
import time
import threading
import json
from collections import deque

# Serial port configuration
ser = serial.Serial('/dev/ttyACM0', 38400, timeout=2)
time.sleep(3)

# WebSocket server URI
uri = "ws://192.168.0.25:8080/ws/sensors"

# Global variables
temperature_readings = deque(maxlen=10)  # Store last 10 readings
lock = threading.Lock()
last_sent_temp = None
last_sent_time = 0  # Store last sent timestamp

def read_temperature_from_serial():
    """Thread function to read temperature from serial port"""
    while True:
        try:
            if ser.in_waiting > 0:
                data = ser.readline().decode('utf-8').strip()
                try:
                    temp = float(data)
                    with lock:
                        temperature_readings.append(temp)
                    print(f"Read temperature: {temp}")
                except ValueError:
                    print(f"Invalid temperature data: {data}")
        except Exception as e:
            print(f"Serial read error: {e}")
        time.sleep(0.1)

async def send_temperature_data():
    """Async function to send temperature data via WebSocket"""
    global last_sent_temp, last_sent_time

    async with websockets.connect(uri) as websocket:
        while True:
            with lock:
                if not temperature_readings:
                    await asyncio.sleep(1)
                    continue
                avg_temp = sum(temperature_readings) / len(temperature_readings)

            current_time = time.time()
            time_since_last_sent = current_time - last_sent_time
            should_send = False

            # Decision logic for sending data
            if last_sent_temp is None or abs(avg_temp - last_sent_temp) > 0.5:
                should_send = True  # Significant change, send immediately
            elif time_since_last_sent >= 300:
                should_send = True  # Send every 5 minutes if stable

            if should_send:
                last_sent_temp = avg_temp
                last_sent_time = current_time
                payload = json.dumps({"temperature": avg_temp})
                await websocket.send(payload)
                print(f"Sent temperature: {avg_temp}")

            await asyncio.sleep(1)  # Check every second for significant changes

async def receive_websocket_instructions():
    """Async function to listen for WebSocket responses and send them via serial"""
    while True:
        try:
            async with websockets.connect(uri) as websocket:
                async for message in websocket:
                    try:
                        instruction = json.loads(message)
                        print(f"Received instruction: {instruction}")
                        ser.write((json.dumps(instruction) + "\n").encode('utf-8'))
                    except json.JSONDecodeError:
                        print(f"Invalid JSON instruction received: {message}")
        except Exception as e:
            print(f"WebSocket listener error: {e}")
            await asyncio.sleep(5)  # Retry connection after 5 seconds

async def main():
    # Start serial reading thread
    serial_thread = threading.Thread(target=read_temperature_from_serial, daemon=True)
    serial_thread.start()

    # Start WebSocket tasks
    await asyncio.gather(send_temperature_data(), receive_websocket_instructions())

if __name__ == "__main__":
    try:
        asyncio.run(main())
    except KeyboardInterrupt:
        print("Stopping...")
    finally:
        ser.close()