#!/bin/bash -ex

seconds=`date +%s`
./submitOrders.py --price 4 --quantity 6 --customerKey BrokerA --clientOrderId A${seconds} --instrumentId 0

./submitOrders.py --price 4 --quantity -4 --customerKey BrokerB --clientOrderId B${seconds} --instrumentId 0

./getOrders.py  --customerKey BrokerA

./getOrders.py  --customerKey BrokerB
