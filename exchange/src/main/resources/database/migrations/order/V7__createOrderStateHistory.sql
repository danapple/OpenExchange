CREATE TABLE IF NOT EXISTS order_state_history (
  orderStateHistoryId BIGINT PRIMARY KEY,
  orderId BIGINT REFERENCES order_base NOT NULL,
  orderStatus VARCHAR NOT NULL,
  createTime BIGINT NOT NULL,
  versionNumber INT NOT NULL
);

ALTER TABLE order_state ADD COLUMN updateTime BIGINT NOT NULL;