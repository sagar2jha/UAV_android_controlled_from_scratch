
import socket
import serial
import time
ser = serial.Serial("/dev/ttyUSB0", 57600, timeout=2)
# Here we define the UDP IP address as well as the port number that we have
# already defined in the client python script.
UDP_IP_ADDRESS = "192.168.1.104"
UDP_PORT_NO = 7000
prev_val=[0,0,0,0]
# declare our serverSocket upon which
# we will be listening for UDP messages
serverSock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
# One difference is that we will have to bind our declared IP address
# and port number to our newly declared serverSock
serverSock.bind((UDP_IP_ADDRESS, UDP_PORT_NO))
while True:
    data, addr = serverSock.recvfrom(1024)
    values = data.split(',')
    thr=int(values[1][(values[1].index(':'))+1:])
    thr=1000+(thr*10)
    roll=int(values[4][(values[4].index(':'))+1:(values[4].index('}'))])
    roll=1000+((50+roll)*10);
    yaw=int(values[2][(values[2].index(':'))+1:])
    yaw=1000+((50+yaw)*10);
    pitch=int(values[3][(values[3].index(':'))+1:])
    pitch=1000+((50+pitch)*10);
    if(thr!= prev_val[0] or yaw!= prev_val[1] or pitch!= prev_val[3] or roll!= prev_val[2]):
        print thr,";",yaw,";",pitch,";",roll
        ser.write(str('a').encode()+str(thr).encode()+str(",").encode()+str(yaw).encode()+str(",").encode()+str(pitch).encode()+str(",").encode()+str()+str(roll).encode()+str(":").encode())
        prev_val[0]=thr
        prev_val[1]=yaw
        prev_val[2]=roll
        prev_val[3]=pitch
