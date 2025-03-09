#!/usr/bin/python3.8
import requests

req = { "behavior": "ALWAYS", \
    "originalClientOrderId": "asdf", \
    "price": 4.3, \
    "quantity": 6, \
    "legs": [ \
        {"ratio": -1, "instrumentId": 34} \
        ]\
    }

cookies = { "customerId": "danapple" }

print ('Requesting')
print ('req', req)

r = requests.put('http://localhost:8080/order/45' , json=req, cookies=cookies, verify=False)

print ('Response code')
print(r)
