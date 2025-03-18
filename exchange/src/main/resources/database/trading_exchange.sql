-- Created using ChatGPT

CREATE TABLE trading_exchanges (
    exchange_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    exchange_symbol VARCHAR(50) UNIQUE NOT NULL,
    timezone VARCHAR(50) NOT NULL
);