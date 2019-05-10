import numpy as np
import cv2
import qrtools
qr = qrtools.QR()

cap = cv2.VideoCapture(1)

while(True):
    # Capture frame-by-frame
    ret, frame = cap.read()
    cv2.imwrite('img.png',frame)
    # Our operations on the frame come here
    qr.decode('img.png')

    # Display the resulting frame
    cv2.imshow('frame',frame)
    if qr.data != 'NULL':
        print qr.data 
    if cv2.waitKey(1) & 0xFF == ord('q'):
        break

# When everything done, release the capture
cap.release()
cv2.destroyAllWindows()
