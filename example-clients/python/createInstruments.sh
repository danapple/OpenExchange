ONLY_OPTIONS=True

./createInstruments.py  --symbol="MI" --description="Megalith Industries" --currencyCode=EUR --numberOfExpiries=4 --numberOfStrikes=6 --baseStrike=100 --strikeIncrement=5 --onlyOptions=${ONLY_OPTIONS}
exit 0

./createInstruments.py  --symbol="BTL" --description="Bai Tu Long Tours" --currencyCode=VND --numberOfExpiries=4 --numberOfStrikes=6 --baseStrike=100 --strikeIncrement=5 --onlyOptions=${ONLY_OPTIONS}
./createInstruments.py  --symbol="SLM" --description="Sword Lake Music" --currencyCode=VND --numberOfExpiries=4 --numberOfStrikes=6 --baseStrike=40 --strikeIncrement=1 --onlyOptions=${ONLY_OPTIONS}

./createInstruments.py  --symbol="ASN" --description="Aerial Sonata" --currencyCode=USD --numberOfExpiries=3 --numberOfStrikes=6 --baseStrike=4 --strikeIncrement=.5 --onlyOptions=${ONLY_OPTIONS}
./createInstruments.py  --symbol="MRO" --description="Mount Rushmore Overlook" --currencyCode=USD --numberOfExpiries=3 --numberOfStrikes=6 --baseStrike=66 --strikeIncrement=5 --onlyOptions=${ONLY_OPTIONS}

./createInstruments.py  --symbol="BST" --description="Boutique Software" --currencyCode=EUR --numberOfExpiries=3 --numberOfStrikes=8 --baseStrike=400 --strikeIncrement=10 --onlyOptions=${ONLY_OPTIONS}
./createInstruments.py  --symbol="PTC" --description="PT Cruises" --currencyCode=EUR --numberOfExpiries=3 --numberOfStrikes=8 --baseStrike=150 --strikeIncrement=10 --onlyOptions=${ONLY_OPTIONS}
./createInstruments.py  --symbol="DFT" --description="Dutch Fiets Tours" --currencyCode=EUR --numberOfExpiries=3 --numberOfStrikes=8 --baseStrike=2 --strikeIncrement=.1 --onlyOptions=${ONLY_OPTIONS}
