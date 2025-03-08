#!/usr/bin/python3.8
import requests

req = { "originalClientOrderId": "asdf", "price": 4.3, "quantity": 4, "instrumentId": 34 }

cookies = { "customerId": "danapple" }

r = requests.delete('http://localhost:8080/order/45' , json=req, cookies=cookies, verify=False)

print ('Response code')
print(r)
