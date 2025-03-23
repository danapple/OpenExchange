#!/usr/bin/python3.8
import getopt
import requests
import sys


def main(argv):

    customerKey=''
    price=''
    quantity=''
    instrumentId=''
    clientOrderId=''
    try:
       opts, args = getopt.getopt(argv, "", ["clientOrderId=","customerKey=","instrumentId=","price=","quantity="])
    except getopt.GetoptError:
       print ('oops')
       sys.exit(2)
    for opt, arg in opts:
         if opt == '--price':
             price = arg
         if opt == '--customerKey':
             customerKey=arg
         if opt == '--quantity':
             quantity=arg
         if opt == '--clientOrderId':
             clientOrderId=arg
         if opt == '--instrumentId':
             instrumentId=arg


    req = { "orders" : [ { "clientOrderId" : clientOrderId, \
                           "price": price, \
                           "quantity": quantity, \
                           "legs": [ \
                               {"ratio": 1, "instrumentId": instrumentId} \
                               ]\
                           }
                         ] }

    cookies = { "customerKey": customerKey }

    #path="http://openexchange.eu-central-1.elasticbeanstalk.com/order/" + clientOrderId
    path="http://localhost:5000/orders"

    print ('Requesting at path', path)
    print ('req', req)

    r = requests.post(path , json=req, cookies=cookies, verify=False)

    print ('Response')
    print(r)
    print(r.json())


if __name__ == "__main__":
   main(sys.argv[1:])
