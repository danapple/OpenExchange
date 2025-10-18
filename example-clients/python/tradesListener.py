#!/usr/bin/python3.12
import stomper
import websocket
#websocket.enableTrace(True)

def on_message(_, message):
   # print(message)

    frame = stomper.Frame()
    unpacked_msg = stomper.Frame.unpack(frame, message)
    print("Received the application message: " + str(unpacked_msg))

def on_ping(wsapp, message):
    print("Got a ping! A pong reply has already been automatically sent.")
def on_pong(wsapp, message):
    print("Got a pong! No need to respond")

def on_open(ws):
    print("on_open")
    # conn = stomper.connect("", "", "")
    # ws.send(conn)
    ws.send("CONNECT\naccept-version:1.0,1.1,2.0\n\n\x00\n")

    sub = stomper.subscribe("/topics/trades", 11, ack='client')
    ws.send(sub)
    # sub = stomper.subscribe("/topics/depth", 21, ack='client')
    # ws.send(sub)

    # msg = stomper.send("/app/echotest", "me oh my")
    # ws.send(msg)

ws_app = websocket.WebSocketApp("ws://localhost:5213/ws", cookie = 'customerKey=BrokerA',
                                on_open=on_open, on_message=on_message, on_ping=on_ping, on_pong=on_pong)

ws_app.run_forever(reconnect=1, ping_interval=10)
