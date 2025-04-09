import asyncio
import websockets
import serial
import time
import threading
import os
import json
import binascii
from pn532pi import Pn532, pn532
from pn532pi import Pn532I2c
from pn532pi import Pn532Spi
from pn532pi import Pn532Hsu

# WebSocket server URI
uri = "ws://192.168.0.25:8080/ws/sensors"

# === SETARE INTERFAȚĂ PN532 ===
SPI = False
I2C = True
HSU = False

if SPI:
    PN532_SPI = Pn532Spi(Pn532Spi.SS0_GPIO8)
    nfc = Pn532(PN532_SPI)
elif HSU:
    PN532_HSU = Pn532Hsu(0)
    nfc = Pn532(PN532_HSU)
elif I2C:
    PN532_I2C = Pn532I2c(1)
    nfc = Pn532(PN532_I2C)

# === VARIABILE GLOBALE ===
latest_temperature = None
last_received_time = time.time()
resetting_usb = False

USB_RESET_CMD = "sudo uhubctl -l 1-1 -a off && sleep 2 && sudo uhubctl -l 1-1 -a on"

# === FUNCȚII ===

def setup_nfc():
    nfc.begin()
    versiondata = nfc.getFirmwareVersion()
    if not versiondata:
        print("Didn't find PN53x board")
        raise RuntimeError("Didn't find PN53x board")

    print("Found chip PN5 {:#x} Firmware ver. {:d}.{:d}".format(
        (versiondata >> 24) & 0xFF,
        (versiondata >> 16) & 0xFF,
        (versiondata >> 8) & 0xFF)
    )

    nfc.setPassiveActivationRetries(0xFF)
    nfc.SAMConfig()
    print("[NFC] Waiting for an ISO14443A card...")


def init_serial():
    global ser
    try:
        ser = serial.Serial('/dev/ttyACM0', 38400, timeout=1)
        time.sleep(1)
        print("[SERIAL] Conexiune serială inițializată.")
    except Exception as e:
        print(f"[ERROR] Eroare la inițializarea serialului: {e}")


def read_serial():
    global latest_temperature, last_received_time, resetting_usb
    while True:
        if resetting_usb:
            time.sleep(1)
            continue
        try:
            if ser.in_waiting > 0:
                data = ser.readline().decode('utf-8').strip()
                last_received_time = time.time()  # MUTAT SUS — se actualizează imediat
                if data.upper() == "PIR":
                    print("[SERIAL] Detecție PIR!")
                    # Trimite WS într-un thread separat, ca să nu blocheze
                    payload = json.dumps({"PIR": 1})
                    threading.Thread(target=lambda: asyncio.run(send_to_websocket(payload)), daemon=True).start()
                else:
                    try:
                        temp = float(data)
                        latest_temperature = temp
                        print(f"[SERIAL] Citit: {temp}")
                    except ValueError:
                        print(f"[SERIAL] Date invalide: {data}")
        except Exception as e:
            print(f"[ERROR] Citire serial: {e}")


def send_temperature():
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


def read_nfc():
    """Thread pentru citirea cardurilor NFC"""
    while True:
        if resetting_usb:
            time.sleep(1)
            continue
        try:
            success, uid = nfc.readPassiveTargetID(pn532.PN532_MIFARE_ISO14443A_106KBPS)
            if success:
                uid_hex = binascii.hexlify(uid).decode("utf-8").upper()
                print(f"[NFC] Card detectat! UID: {uid_hex}")
                payload = json.dumps({"RFID": uid_hex})
                threading.Thread(target=lambda: asyncio.run(send_to_websocket(payload)), daemon=True).start()
                time.sleep(1.5)
        except Exception as e:
            print(f"[ERROR] NFC: {e}")
            time.sleep(1)


async def send_to_websocket(payload):
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
    global last_received_time, resetting_usb, ser
    while True:
        time.sleep(1)
        if time.time() - last_received_time > 5 and not resetting_usb:
            print("[WATCHDOG] Nu s-a primit nimic în ultimele 5 secunde! Reset USB...")
            resetting_usb = True
            try:
                ser.close()
                print("[SERIAL] Conexiune serială închisă.")
            except Exception as e:
                print(f"[ERROR] Închidere serial: {e}")
            os.system(USB_RESET_CMD)
            time.sleep(5)
            init_serial()
            resetting_usb = False

# === EXECUȚIE ===

init_serial()
setup_nfc()

threading.Thread(target=read_serial, daemon=True).start()
threading.Thread(target=send_temperature, daemon=True).start()
threading.Thread(target=read_nfc, daemon=True).start()
threading.Thread(target=watchdog, daemon=True).start()

asyncio.run(receive_websocket())