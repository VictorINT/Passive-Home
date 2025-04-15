from ultralytics import YOLO
import easyocr
import cv2
import time
import re
from difflib import SequenceMatcher

model = YOLO('license_plate_detector.pt')

reader = easyocr.Reader(['ro'])

# Deschide camera (0 = /dev/video0, camera USB)
cap = cv2.VideoCapture(1)

if not cap.isOpened():
    print("Camera not found. Check if it s connected.")
    exit()

prev_detected = False
last_plate = ""

print("ANPR system started. Press CTRL+C to stop.\n")

try:
    while True:
        ret, frame = cap.read()
        if not ret:
            print("Failed to grab frame.")
            break

        results = model.predict(source=frame, verbose=False)

        detections = results[0].boxes

        if detections is not None and len(detections) > 0:
            for box in detections:
                x1, y1, x2, y2 = map(int, box.xyxy[0])
                placa = frame[y1:y2, x1:x2]

                ocr_results = reader.readtext(placa)
                for (_, text, conf) in ocr_results:
                    if conf > 0.4:
                        def is_valid_plate(plate):
                            pattern = r'^RO [A-Z]{1,2} \d{3} [A-Z]{3}$'
                            return re.match(pattern, plate)

                        def is_similar_plate(plate):
                            example_format = "RO XX 000 XXX"
                            similarity = SequenceMatcher(None, example_format, plate).ratio()
                            return similarity >= 0.9

                        if is_valid_plate(text):
                            text.replace(" ", "")
                            print(f"Valid plate: {text}")
                        elif is_similar_plate(text):
                            print(f"Almost valid plate: {text}")
                        else:
                            print("Again..bad read")
                        print(f"Detected plate: {text}")

        time.sleep(0.1)

except KeyboardInterrupt:
    print("\nStopped by user.")
