#!/usr/bin/python3.8
import requests
import requests
import sys, getopt

def main(argv):
    customerId=''
    try:
        opts, args = getopt.getopt(argv, "", ["customerId="])
    except getopt.GetoptError:
        print ('oops')
        sys.exit(2)
    for opt, arg in opts:
        if opt == '--customerId':
            customerId=arg

    cookies = { "customerId": customerId }

    path="http://localhost:5000/orders"
    r = requests.get(path, cookies=cookies, verify=False)

    print ('Response')
    print(r)
    print(r.json())
    print ('End of response')

if __name__ == "__main__":
    main(sys.argv[1:])
