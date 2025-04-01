#!/usr/bin/python3.12

import getopt
import requests
import requests
import sys


def main(argv):
    customerKey=''
    try:
        opts, args = getopt.getopt(argv, "", ["customerKey="])
    except getopt.GetoptError:
        print ('oops')
        sys.exit(2)
    for opt, arg in opts:
        if opt == '--customerKey':
            customerKey=arg

    cookies = { "customerKey": customerKey }

    path="http://localhost:5000/orders"
    r = requests.get(path, cookies=cookies, verify=False)

    print ('Response')
    print(r)
    print(r.json())
    print ('End of response')

if __name__ == "__main__":
    main(sys.argv[1:])
