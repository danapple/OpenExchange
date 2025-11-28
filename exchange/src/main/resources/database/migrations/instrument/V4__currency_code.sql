CREATE TABLE currency (
    currencyId SERIAL PRIMARY KEY,
    currencyCode VARCHAR NOT NULL UNIQUE
);

INSERT INTO currency (currencyCode) VALUES
    ('USD'),
    ('EUR'),
    ('GBP'),
    ('ILS'),
    ('JPY'),
    ('VND'),
    ('THB')
;

ALTER TABLE instrument ADD COLUMN currencyCode VARCHAR NOT NULL REFERENCES currency(currencyCode) DEFAULT 'USD';

ALTER TABLE instrument ALTER COLUMN currencyCode DROP DEFAULT;
