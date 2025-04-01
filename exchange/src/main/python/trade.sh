#!/bin/bash -ex

seconds=`date +%s`
./submitOrders.py --price 4 --quantity 2 --customerKey BrokerA --clientOrderId A${seconds} --instrumentId 0

./submitOrders.py --price 4 --quantity -4 --customerKey BrokerB --clientOrderId B${seconds} --instrumentId 0

./submitOrders.py --price 3 --quantity 2 --customerKey BrokerA --clientOrderId C${seconds} --instrumentId 0

./submitOrders.py --price 3 --quantity 2 --customerKey BrokerA --clientOrderId D${seconds} --instrumentId 0

./submitOrders.py --price 5 --quantity -4 --customerKey BrokerB --clientOrderId E${seconds} --instrumentId 0

./submitOrders.py --price 6 --quantity -4 --customerKey BrokerB --clientOrderId F${seconds} --instrumentId 0

./getOrders.py  --customerKey BrokerA

./getOrders.py  --customerKey BrokerB
