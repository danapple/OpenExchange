#!/usr/bin/python3.8
import getopt
import requests
import sys


def main(argv):
    customerKey=''
    clientOrderId=''
    try:
        opts, args = getopt.getopt(argv, "", ["clientOrderId=","customerKey="])
    except getopt.GetoptError:
        print ('oops')
        sys.exit(2)
    for opt, arg in opts:
        if opt == '--customerKey':
            customerKey=arg
        if opt == '--clientOrderId':
            clientOrderId=arg

        cookies = { "customerKey": customerKey }

    path="http://localhost:5213/orders/" + clientOrderId
    r = requests.delete(path , cookies=cookies, verify=False)

    print ('Response code')
    print(r)
    print(r.json())

if __name__ == "__main__":
    main(sys.argv[1:])
