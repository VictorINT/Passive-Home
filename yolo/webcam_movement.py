import cv2

cap = cv2.VideoCapture(0)

_, frame1 = cap.read()
gray1 = cv2.cvtColor(frame1, cv2.COLOR_BGR2GRAY)
gray1 = cv2.GaussianBlur(gray1, (21, 21), 0)

while True:
    ret, frame2 = cap.read()
    if not ret:
        break

    gray2 = cv2.cvtColor(frame2, cv2.COLOR_BGR2GRAY)
    gray2 = cv2.GaussianBlur(gray2, (21, 21), 0)

    delta = cv2.absdiff(gray1, gray2)
    thresh = cv2.threshold(delta, 25, 255, cv2.THRESH_BINARY)[1]
    thresh = cv2.dilate(thresh, None, iterations=2)

    contours, _ = cv2.findContours(thresh, cv2.RETR_EXTERNAL, cv2.CHAIN_APPROX_SIMPLE)

    for c in contours:
        if cv2.contourArea(c) < 500:
            continue 
        (x, y, w, h) = cv2.boundingRect(c)
        cv2.rectangle(frame2, (x, y), (x + w, y + h), (0, 255, 0), 2)

    cv2.imshow("Miscarile detectate", frame2)

    gray1 = gray2.copy()

    if cv2.waitKey(1) & 0xFF == ord('q'):
        break

cap.release()
cv2.destroyAllWindows()
