#!/usr/bin/python3.12
import calendar
import getopt
import requests
import sys
from datetime import *;
from dateutil.relativedelta import *

def print_option_pair(symbol, expiry, strike, currency_code):
    expiration_time = int(expiry.timestamp() * 1000)
    strike_symbol = int(strike * 100)

    symbol_expiration = f"{expiry.year}{expiry.month}{expiry.day}"
    description_expiration = f"{expiry.year}-{expiry.month}-{expiry.day}"

    call_symbol = f"{symbol}{symbol_expiration}C{strike_symbol}"
    put_symbol = f"{symbol}{symbol_expiration}P{strike_symbol}"

    description_strike = '{0:.2f}'.format(strike)

    print()

    print("INSERT INTO instrument (status, symbol, assetClass, description, expirationTime, currencyCode) "
          " VALUES ( "
          " 'ACTIVE', "
          f"'{call_symbol}', "
          f"'OPTION', "
          f"'{symbol} {description_expiration} {description_strike} Call', "
          f"'{expiration_time}', "
          f"'{currency_code}' "
          " );"
          )

    print("INSERT INTO derivative (instrumentId, underlyingInstrumentId, valuefactor) "
          " VALUES ( "
          f" (select instrumentId from instrument where symbol = '{call_symbol}' ), "
          f" (select instrumentId from instrument where symbol = '{symbol}' ), "
          "100"
          " );"
          )

    print("INSERT INTO option (instrumentId, optionType, strike) "
          " VALUES ("
          f" (select instrumentId from instrument where symbol = '{call_symbol}' ), "
          "'CALL', "
          f"'{strike}'"
          " );"
          )

    print("INSERT INTO instrument (status, symbol, assetClass, description, expirationTime, currencyCode) "
          " VALUES ( "
          " 'ACTIVE', "
          f"'{put_symbol}', "
          f"'OPTION', "
          f"'{symbol} {description_expiration} {description_strike} Put', "
          f"'{expiration_time}', "
          f"'{currency_code}' "
          " );"
          )
    print("INSERT INTO derivative (instrumentId, underlyingInstrumentId, valuefactor) "
          " VALUES ( "
          f" (select instrumentId from instrument where symbol = '{put_symbol}' ), "
          f" (select instrumentId from instrument where symbol = '{symbol}' ), "
          "100"
          " );"
          )
    print("INSERT INTO option (instrumentId, optionType, strike) "
          " VALUES ("
          f" (select instrumentId from instrument where symbol = '{put_symbol}' ), "
          "'PUT', "
          f"'{strike}'"
          " );"
          )

def main(argv):
    symbol=""
    number_of_strikes=0
    number_of_expiries=0
    strike_increment=0.05
    base_strike=100
    currency_code="USD"
    asset_class="EQUITY"
    description=""
    only_options='False'
    try:
        opts, args = getopt.getopt(argv, "", ["symbol=","numberOfStrikes=","numberOfExpiries=","strikeIncrement=","baseStrike=","currencyCode=","assetClass=","description=","onlyOptions="])
    except getopt.GetoptError:
        print ('oops')
        sys.exit(2)
    for opt, arg in opts:
        if opt == '--symbol':
            symbol=arg
        if opt == '--numberOfStrikes':
            number_of_strikes=int(arg)
        if opt == '--numberOfExpiries':
            number_of_expiries=int(arg)
        if opt == '--strikeIncrement':
            strike_increment=float(arg)
        if opt == '--baseStrike':
            base_strike=int(arg)
        if opt == '--currencyCode':
            currency_code=arg
        if opt == '--assetClass':
            asset_class=arg
        if opt == '--description':
            description=arg
        if opt == '--onlyOptions':
            only_options=arg

    print ("begin;")
    expiration_time = 0
    if only_options == 'False':
        print("INSERT INTO instrument (status, symbol, assetClass, description, expirationTime, currencyCode) "
              " VALUES ("
              " 'ACTIVE', "
              f"'{symbol}', "
              f"'{asset_class}', "
              f"'{description}', "
              f"'{expiration_time}', "
              f"'{currency_code}' "
              " );"
              )
        print("INSERT INTO equity (instrumentId) "
              " VALUES ("
              f" (select instrumentId from instrument where symbol = '{symbol}' )"
              " );")

    third_thursday = datetime.today()+relativedelta(day=31, weekday=TH(-1), hour=23, minute=59, second=59, microseconds=100000)
    expiry_thursday = third_thursday
    for expiry_index in range (0, number_of_expiries):
        if expiry_thursday < datetime.today():
            expiry_thursday = expiry_thursday + relativedelta(months=+1, day=31, weekday=TH(-1))
            continue
        for strike_index in range (0, int(number_of_strikes / 2)):
            strike_up=base_strike + strike_index * strike_increment
            print_option_pair(symbol, expiry_thursday, strike_up, currency_code)
            strike_down=base_strike - strike_index * strike_increment
            if strike_down != strike_up:
                print_option_pair(symbol, expiry_thursday, strike_down, currency_code)
        expiry_thursday = expiry_thursday + relativedelta(months=+1, day=31, weekday=TH(-1))

    print ("commit;")

if __name__ == "__main__":
    main(sys.argv[1:])
