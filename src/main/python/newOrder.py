#!/usr/bin/python3.8
import requests
import sys, getopt

def main(argv):

    customerId=''
    price=''
    quantity=''
    instrumentId=''
    clientOrderId=''
    try:
       opts, args = getopt.getopt(argv, "", ["clientOrderId=","customerId=","instrumentId=","price=","quantity="])
    except getopt.GetoptError:
       print ('oops')
       sys.exit(2)
    for opt, arg in opts:
         if opt == '--price':
             price = arg
         if opt == '--customerId':
             customerId=arg
         if opt == '--quantity':
             quantity=arg
         if opt == '--clientOrderId':
             clientOrderId=arg
         if opt == '--instrumentId':
             instrumentId=arg

    req = { "price": price, \
         "quantity": quantity, \
         "legs": [ \
         {"ratio": 1, "instrumentId": instrumentId} \
         ]\
    }

    cookies = { "customerId": customerId }

    path="http://localhost:8080/order/" + clientOrderId
    print ('Requesting at path', path)
    print ('req', req)

    r = requests.post(path , json=req, cookies=cookies, verify=False)

    print ('Response')
    print(r)
    print(r.json())


if __name__ == "__main__":
   main(sys.argv[1:])