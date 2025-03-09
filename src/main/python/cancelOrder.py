#!/usr/bin/python3.8
import requests
import sys, getopt

def main(argv):
    customerId=''
    clientOrderId=''
    try:
        opts, args = getopt.getopt(argv, "", ["clientOrderId=","customerId="])
    except getopt.GetoptError:
        print ('oops')
        sys.exit(2)
    for opt, arg in opts:
        if opt == '--customerId':
            customerId=arg
        if opt == '--clientOrderId':
            clientOrderId=arg

        cookies = { "customerId": customerId }

    path="http://localhost:8080/order/" + clientOrderId
    r = requests.delete(path , cookies=cookies, verify=False)

    print ('Response code')
    print(r)
    print(r.json())

if __name__ == "__main__":
    main(sys.argv[1:])
