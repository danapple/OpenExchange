#!/usr/bin/python3.8
import requests
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

    path="http://localhost:5000/order/" + clientOrderId
    r = requests.get(path, cookies=cookies, verify=False)

    print ('Response')
    print(r)
    print(r.json())
    print ('End of response')

if __name__ == "__main__":
    main(sys.argv[1:])
