import qrtools
qr = qrtools.QR()
qr.decode("qr4.jpg")
print qr.data
