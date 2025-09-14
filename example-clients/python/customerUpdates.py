import websocket
from websocket import create_connection

ws = create_connection("ws://localhost:8080/ws")

client_id = 1
sub = stom.javaper.subscribe("/topics/orders", client_id, ack='auto')
ws.send(sub)
ws.send("LTCBTC")
while True:

    result = ws.recv()
    print ("Received '%s'" % result)

ws.close()