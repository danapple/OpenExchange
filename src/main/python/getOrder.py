#!/usr/bin/python3.8
import requests

cookies = { "customerId": "danapple" }

r = requests.get('http://localhost:8080/order/45', cookies=cookies, verify=False)

print ('Response code')
print(r)
