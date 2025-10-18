#!/usr/bin/python3.12
import stomper
import websocket
websocket.enableTrace(True)

def on_message(_, message):
    # print(message)

    frame = stomper.Frame()
    unpacked_msg = stomper.Frame.unpack(frame, message)
    print("Received the application message: " + str(unpacked_msg))

def on_open(ws):
    ws.send("CONNECT\naccept-version:1.0,1.1,2.0\n\n\x00\n")

    sub = stomper.subscribe("/topics/executions/BrokerA", 1, ack='auto')
    ws.send(sub)

ws_app = websocket.WebSocketApp("ws://localhost:5213/ws", cookie = 'customerKey=BrokerA', on_open=on_open, on_message=on_message)

ws_app.run_forever(reconnect=1)



# ws.connect("ws://localhost:5213/ws")
#
# client_id = 1
# sub = stom.javaper.subscribe("/topics/orders", client_id, ack='auto')
# ws.send(sub)
# ws.send("LTCBTC")
# while True:
#
#     result = ws.recv()
#     print ("Received '%s'" % result)
#
# ws.close()