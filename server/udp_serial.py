import serial
import time
ser = serial.Serial("/dev/ttyUSB0", 9600, timeout=2)
data=1500
data1=1200
time.sleep(2)
while(True):
    ser.write(data)
    time.sleep(2)
    ser.write(data1)
