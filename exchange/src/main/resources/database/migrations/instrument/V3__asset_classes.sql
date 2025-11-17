CREATE TABLE equity (
  instrumentId BIGINT NOT NULL PRIMARY KEY  REFERENCES instrument
);

CREATE TABLE derivative (
  instrumentId BIGINT NOT NULL PRIMARY KEY REFERENCES instrument,
  underlyingInstrumentId BIGINT NOT NULL REFERENCES instrument(instrumentId),
  valueFactor FLOAT NOT NULL
);

CREATE TABLE deliverable (
  instrumentId BIGINT NOT NULL REFERENCES derivative,
  deliverableInstrumentId BIGINT NOT NULL REFERENCES instrument(instrumentId),
  quantity INT NOT NULL
);

CREATE UNIQUE INDEX unq_deliverable ON deliverable (instrumentId, deliverableInstrumentId);

CREATE TABLE cash_deliverable (
  instrumentId BIGINT NOT NULL PRIMARY KEY REFERENCES derivative,
  value FLOAT NOT NULL
);

CREATE TYPE OPTION_TYPE AS ENUM ('CALL', 'PUT');

CREATE TABLE option (
  instrumentId BIGINT NOT NULL PRIMARY KEY REFERENCES derivative,
  optionType OPTION_TYPE NOT NULL,
  strike FLOAT NOT NULL
);

CREATE TABLE future (
  instrumentId BIGINT NOT NULL PRIMARY KEY REFERENCES derivative
);