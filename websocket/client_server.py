import asyncio
import websockets
import serial
import time
import threading
import os
import json

# WebSocket server URI
uri = "ws://192.168.0.25:8080/ws/sensors"

# Variabile partajate
latest_temperature = None
last_received_time = time.time()  # Pentru watchdog
resetting_usb = False  # Flag pentru oprirea execuției în timpul resetării

# Comandă pentru resetarea USB cu uhubctl (modifică ID-ul hub-ului)
USB_RESET_CMD = "sudo uhubctl -l 1-1 -a off && sleep 2 && sudo uhubctl -l 1-1 -a on"

# Funcție pentru inițializarea conexiunii seriale
def init_serial():
    global ser
    try:
        ser = serial.Serial('/dev/ttyACM0', 38400, timeout=1)
        time.sleep(1)
        print("[SERIAL] Conexiune serială inițializată.")
    except Exception as e:
        print(f"[ERROR] Eroare la inițializarea serialului: {e}")

# Inițializează conexiunea serială la pornire
init_serial()

def read_temperature():
    """Thread pentru citirea continuă de la serial"""
    global latest_temperature, last_received_time, resetting_usb
    while True:
        if resetting_usb:  
            time.sleep(1)
            continue
        try:
            if ser.in_waiting > 0:
                data = ser.readline().decode('utf-8').strip()
                last_received_time = time.time()  
                try:
                    temp = float(data)
                    latest_temperature = temp  
                    print(f"[SERIAL] Citit: {temp}")  
                except ValueError:
                    print(f"[SERIAL] Date invalide: {data}")
        except Exception as e:
            print(f"[ERROR] Citire serial: {e}")

def send_temperature():
    """Thread pentru trimiterea datelor la fiecare 30 secunde"""
    global latest_temperature, resetting_usb
    while True:
        if resetting_usb:  
            time.sleep(1)
            continue
        try:
            time.sleep(30)  
            if latest_temperature is not None:
                payload = json.dumps({"temperature": latest_temperature})
                asyncio.run(send_to_websocket(payload))
        except Exception as e:
            print(f"[ERROR] Trimitere WebSocket: {e}")

async def send_to_websocket(payload):
    """Trimite un mesaj WebSocket"""
    global resetting_usb
    if resetting_usb:
        return  
    try:
        async with websockets.connect(uri) as websocket:
            await websocket.send(payload)
            print(f"[WS] Trimis: {payload}")
    except Exception as e:
        print(f"[WS ERROR] Conexiune WebSocket: {e}")

async def receive_websocket():
    """Ascultă WebSocket și trimite comenzi pe serial"""
    global resetting_usb
    while True:
        if resetting_usb:
            await asyncio.sleep(1)
            continue
        try:
            async with websockets.connect(uri) as websocket:
                async for message in websocket:
                    if resetting_usb:
                        break  
                    try:
                        ser.write((message + "\n").encode('utf-8'))
                        print(f"[WS] Primit și trimis pe serial: {message}")
                    except Exception as e:
                        print(f"[ERROR] Scriere serial: {e}")
        except Exception as e:
            print(f"[WS ERROR] Conexiune WebSocket pierdută: {e}")
            await asyncio.sleep(5) 

def watchdog():
    """Monitorizează timpul ultimei citiri și resetează USB dacă e necesar"""
    global last_received_time, resetting_usb, ser
    while True:
        time.sleep(1)
        if time.time() - last_received_time > 5 and not resetting_usb:
            print("[WATCHDOG] Nu s-a primit nimic în ultimele 5 secunde! Reset USB...")
            resetting_usb = True  

            # Închide conexiunea serială înainte de resetare
            try:
                ser.close()
                print("[SERIAL] Conexiune serială închisă.")
            except Exception as e:
                print(f"[ERROR] Închidere serial: {e}")

            os.system(USB_RESET_CMD)  
            time.sleep(5)  

            init_serial()

            resetting_usb = False  

threading.Thread(target=read_temperature, daemon=True).start()
threading.Thread(target=send_temperature, daemon=True).start()
threading.Thread(target=watchdog, daemon=True).start()
asyncio.run(receive_websocket())  # Start WebSocket listener
