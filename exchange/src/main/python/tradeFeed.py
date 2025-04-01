#!/usr/bin/python3.8

import socket
import struct
import sys

multicast_group = '227.3.3.3'
server_address = ('', 3436)

# Create the socket
sock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)

# Bind to the server address
sock.bind(server_address)

group = socket.inet_aton(multicast_group)
mreq = struct.pack('4sL', group, socket.INADDR_ANY)
sock.setsockopt(socket.IPPROTO_IP, socket.IP_ADD_MEMBERSHIP, mreq)
sock.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)

# Receive/respond loop
while True:
    print ('\nwaiting to receive message')
    data, address = sock.recvfrom(1024)

    print ('received %s bytes from %s' % (len(data), address))
    print (data)
